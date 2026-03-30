from operacoes.base_operacoes import BaseOperacoesImagem
class MorfismoImagem(BaseOperacoesImagem):
    '''
    Interpola duas imagens com deformacao triangular e mistura temporal.

    Como calcula:
    - Ajusta as duas imagens para uma area comum.
    - Divide a area em 4 triangulos ligados ao centro.
    - Usa coordenadas baricentricas para mapear origem em A e B.
    - Combina os niveis com (1 - t) * A + t * B.
    '''
    def _inverter_matriz_3x3(self, matriz):
        a, b, c = matriz[0]
        d, e, f = matriz[1]
        g, h, i = matriz[2]

        det = (
            a * (e * i - f * h)
            - b * (d * i - f * g)
            + c * (d * h - e * g)
        )

        if abs(det) < 1e-12:
            return None

        cofatores = [
            [e * i - f * h, -(d * i - f * g), d * h - e * g],
            [-(b * i - c * h), a * i - c * g, -(a * h - b * g)],
            [b * f - c * e, -(a * f - c * d), a * e - b * d],
        ]

        adjunta = [
            [cofatores[0][0], cofatores[1][0], cofatores[2][0]],
            [cofatores[0][1], cofatores[1][1], cofatores[2][1]],
            [cofatores[0][2], cofatores[1][2], cofatores[2][2]],
        ]

        return [[adjunta[r][c] / det for c in range(3)] for r in range(3)]

    def interpolar_morfismo(self, matriz_a, matriz_b, t=0.5):
        self.validar_matriz(matriz_a)
        self.validar_matriz(matriz_b)

        def limitar_t(valor_t):
            return max(0.0, min(1.0, float(valor_t)))

        def combinar_niveis(pixel_a, pixel_b, fator_t):
            return (1.0 - fator_t) * pixel_a + fator_t * pixel_b

        t = limitar_t(t)

        # Prepara as dimensões (Garante que as matrizes tenham o mesmo tamanho para o cálculo)
        altura = min(len(matriz_a), len(matriz_b))
        largura = min(len(matriz_a[0]), len(matriz_b[0]))
        
        # Redimensiona para garantir tamanhos iguais
        img_a = [linha[:largura] for linha in matriz_a[:altura]]
        img_b = [linha[:largura] for linha in matriz_b[:altura]]

        # Se t for 0 ou 1, retorna direto para poupar processamento
        if t <= 0.0:
            return [linha[:] for linha in img_a]

        if t >= 1.0:
            return [linha[:] for linha in img_b]

        # PASSO 1 e 2: Definição de pontos de vértice v e w
        # Aqui usamos 5 pontos (4 cantos + centro) para criar a estrutura básica.
        # v representa os pontos na imagem inicial (criança) e w na final (adulto).
        vertices_a = [
            (0.0, 0.0),
            (float(largura - 1), 0.0),
            (float(largura - 1), float(altura - 1)),
            (0.0, float(altura - 1)),
            (float(largura // 2), float(altura // 2)),
        ]

        vertices_b = [
            (0.0, 0.0),
            (float(largura - 1), 0.0),
            (float(largura - 1), float(altura - 1)),
            (0.0, float(altura - 1)),
            (float(largura // 2), float(altura // 2)),
        ]

        # PASSO 3 e 5: Triangulação
        # Dividimos o retângulo em 4 triângulos conectando os cantos ao centro.
        triangulos = [
            (0, 1, 4),
            (1, 2, 4),
            (2, 3, 4),
            (3, 0, 4),
        ]

        # PASSO 4: Encontrar pontos de vértice u(t) no instante t
        # Implementa a Equação (9): u_i(t) = (1 - t)v_i + t*w_i
        # Isso move a "geometria" da imagem ao longo do tempo.
        vertices_t = []
        for (ax, ay), (bx, by) in zip(vertices_a, vertices_b):
            xt = (1.0 - t) * ax + t * bx
            yt = (1.0 - t) * ay + t * by
            vertices_t.append((xt, yt))

        resultado = self.criar_matriz(altura, largura, 0)
        preenchido = self.criar_matriz(altura, largura, False)

        for tri in triangulos:
            ut = [vertices_t[idx] for idx in tri] # Vértices do triângulo deformado no tempo t
            vt = [vertices_a[idx] for idx in tri] # Vértices na imagem A
            wt = [vertices_b[idx] for idx in tri] # Vértices na imagem B

            # PASSO 6 e 7: Combinação Convexa e Coordenadas Baricêntricas
            # Invertemos a matriz de vértices para encontrar os coeficientes c1, c2, c3.
            # Isso resolve as Equações (10) e (11).
            matriz_u = [
                [ut[0][0], ut[1][0], ut[2][0]],
                [ut[0][1], ut[1][1], ut[2][1]],
                [1.0, 1.0, 1.0],
            ]
            inv_u = self._inverter_matriz_3x3(matriz_u)
            if inv_u is None:
                continue

            # Preparação para processamento iterativo pixel a pixel
            for i in range(altura):
                for j in range(largura):
                    
                    # Calcula os pesos baricêntricos para o pixel atual
                    c1 = j * inv_u[0][0] + i * inv_u[0][1] + inv_u[0][2]
                    c2 = j * inv_u[1][0] + i * inv_u[1][1] + inv_u[1][2]
                    c3 = j * inv_u[2][0] + i * inv_u[2][1] + inv_u[2][2]

                    # Filtra apenas os pixels que pertencem ao triângulo atual (c1, c2, c3 >= 0)
                    if c1 < -0.001 or c2 < -0.001 or c3 < -0.001:
                        continue

                    # PASSO 8: Determinar a localização do ponto u nas imagens original e final
                    # Equação (12): v = c1*v_i + c2*v_j + c3*v_k (Posição de onde tirar a cor em A)
                    src_a_x = int(c1 * vt[0][0] + c2 * vt[1][0] + c3 * vt[2][0])
                    src_a_y = int(c1 * vt[0][1] + c2 * vt[1][1] + c3 * vt[2][1])
                    
                    # Equação (13): w = c1*w_i + c2*w_j + c3*w_k (Posição de onde tirar a cor em B)
                    src_b_x = int(c1 * wt[0][0] + c2 * wt[1][0] + c3 * wt[2][0])
                    src_b_y = int(c1 * wt[0][1] + c2 * wt[1][1] + c3 * wt[2][1])

                    src_a_x = max(0, min(largura - 1, src_a_x))
                    src_a_y = max(0, min(altura - 1, src_a_y))
                    src_b_x = max(0, min(largura - 1, src_b_x))
                    src_b_y = max(0, min(altura - 1, src_b_y))

                    # PASSO 9: Média ponderada dos níveis de cinza (Densidade de Imagem)
                    # Implementa a Equação (14): p_t(u) = (1 - t)p_0(v) + t*p_1(w)
                    # Esta é a mistura final das cores após a deformação geométrica.
                    valor = combinar_niveis(
                        img_a[src_a_y][src_a_x],
                        img_b[src_b_y][src_b_x],
                        t,
                    )

                    resultado[i][j] = self.limitar(valor)
                    preenchido[i][j] = True

        # Preenchimento de falhas de arredondamento
        for i in range(altura):
            for j in range(largura):
                if preenchido[i][j]:
                    continue

                valor = combinar_niveis(img_a[i][j], img_b[i][j], t)
                resultado[i][j] = self.limitar(valor)

        return resultado

