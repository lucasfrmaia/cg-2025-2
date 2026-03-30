import math
from operacoes.base_operacoes import BaseOperacoesImagem


class TransformacoesGeometricasImagem(BaseOperacoesImagem):
    '''
    Redimensiona a imagem por fatores em X e Y.

    Como calcula:
    - Calcula novo tamanho com os fatores.
    - Para cada pixel de saida, busca origem por vizinho mais proximo.
    '''
    def escala(self, matriz, fator_x=1.0, fator_y=None, valor_fundo=0):
        self.validar_matriz(matriz)

        if fator_y is None:
            fator_y = fator_x

        if fator_x <= 0 or fator_y <= 0:
            raise ValueError("Os fatores de escala devem ser maiores que zero.")

        altura = len(matriz)
        largura = len(matriz[0])
        nova_largura = max(1, int(round(largura * fator_x)))
        nova_altura = max(1, int(round(altura * fator_y)))
        saida = self.criar_matriz(nova_altura, nova_largura, valor_fundo)

        for i in range(nova_altura):
            for j in range(nova_largura):
                origem_i = int(i / fator_y)
                origem_j = int(j / fator_x)

                if 0 <= origem_i < altura and 0 <= origem_j < largura:
                    saida[i][j] = matriz[origem_i][origem_j]

        return saida

    '''
    Translada a imagem no plano cartesiano da matriz.

    Como calcula:
    - Para cada destino (i, j), calcula origem (i - dy, j - dx).
    - Se a origem sair da imagem, usa valor de fundo.
    '''
    def translacao(self, matriz, deslocamento_x=0, deslocamento_y=0, valor_fundo=0):
        self.validar_matriz(matriz)

        deslocamento_x = int(round(deslocamento_x))
        deslocamento_y = int(round(deslocamento_y))

        altura = len(matriz)
        largura = len(matriz[0])
        saida = self.criar_matriz(altura, largura, valor_fundo)

        for i in range(altura):
            for j in range(largura):
                origem_i = i - deslocamento_y
                origem_j = j - deslocamento_x

                if 0 <= origem_i < altura and 0 <= origem_j < largura:
                    saida[i][j] = matriz[origem_i][origem_j]

        return saida

    '''
    Rotaciona a imagem e ajusta o quadro de saida.

    Como calcula:
    - Converte o angulo para radianos e calcula cos/sen.
    - Estima novo tamanho pelos cantos rotacionados.
    - Usa transformacao inversa para buscar os pixels de origem.
    '''
    def rotacao(self, matriz, angulo_graus=0.0, valor_fundo=0):
        self.validar_matriz(matriz)

        altura = len(matriz)
        largura = len(matriz[0])
        angulo = math.radians(-angulo_graus)
        cos_a = math.cos(angulo)
        sin_a = math.sin(angulo)

        cx = (largura - 1) / 2.0
        cy = (altura - 1) / 2.0

        cantos = [
            (-cx, -cy),
            (largura - 1 - cx, -cy),
            (-cx, altura - 1 - cy),
            (largura - 1 - cx, altura - 1 - cy),
        ]

        x_rot = []
        y_rot = []
        for x, y in cantos:
            xr = x * cos_a - y * sin_a
            yr = x * sin_a + y * cos_a
            x_rot.append(xr)
            y_rot.append(yr)

        min_x = min(x_rot)
        max_x = max(x_rot)
        min_y = min(y_rot)
        max_y = max(y_rot)

        nova_largura = int(round(max_x - min_x)) + 1
        nova_altura = int(round(max_y - min_y)) + 1
        saida = self.criar_matriz(nova_altura, nova_largura, valor_fundo)

        novo_cx = (nova_largura - 1) / 2.0
        novo_cy = (nova_altura - 1) / 2.0

        for i in range(nova_altura):
            for j in range(nova_largura):
                xr = j - novo_cx
                yr = i - novo_cy

                x_origem = xr * cos_a + yr * sin_a
                y_origem = -xr * sin_a + yr * cos_a

                origem_j = int(round(x_origem + cx))
                origem_i = int(round(y_origem + cy))

                if 0 <= origem_i < altura and 0 <= origem_j < largura:
                    saida[i][j] = matriz[origem_i][origem_j]

        return saida

    '''
    Reflete a imagem em horizontal ou vertical.

    Como calcula:
    - Horizontal: inverte colunas.
    - Vertical: inverte linhas.
    '''
    def reflexao(self, matriz, modo="horizontal"):
        self.validar_matriz(matriz)

        altura = len(matriz)
        largura = len(matriz[0])
        saida = self.criar_matriz(altura, largura, 0)

        if modo == "horizontal":
            for i in range(altura):
                for j in range(largura):
                    saida[i][largura - 1 - j] = matriz[i][j]
            return saida

        if modo == "vertical":
            for i in range(altura):
                for j in range(largura):
                    saida[altura - 1 - i][j] = matriz[i][j]
            return saida

        raise ValueError("Modo de reflexao invalido. Use horizontal ou vertical.")

    '''
    Aplica cisalhamento nos eixos X e Y.

    Como calcula:
    - Aplica matriz de shear com fatores informados.
    - Calcula novo quadro pelos cantos transformados.
    - Usa transformacao inversa para amostrar a imagem original.
    '''
    def cisalhamento(self, matriz, fator_x=0.0, fator_y=0.0, valor_fundo=0):
        self.validar_matriz(matriz)

        shear_x = fator_x
        shear_y = fator_y

        determinante = 1.0 - (shear_x * shear_y)
        if abs(determinante) < 1e-8:
            raise ValueError("Combinacao de fatores gera transformacao nao invertivel.")

        altura = len(matriz)
        largura = len(matriz[0])

        cantos = [
            (0, 0),
            (largura - 1, 0),
            (0, altura - 1),
            (largura - 1, altura - 1),
        ]

        x_transformado = []
        y_transformado = []
        for x, y in cantos:
            xt = x - shear_x * y
            yt = y - shear_y * x
            x_transformado.append(xt)
            y_transformado.append(yt)

        min_x = min(x_transformado)
        max_x = max(x_transformado)
        min_y = min(y_transformado)
        max_y = max(y_transformado)

        nova_largura = int(math.ceil(max_x - min_x)) + 1
        nova_altura = int(math.ceil(max_y - min_y)) + 1
        saida = self.criar_matriz(nova_altura, nova_largura, valor_fundo)

        for i in range(nova_altura):
            for j in range(nova_largura):
                xt = j + min_x
                yt = i + min_y

                x_origem = (xt + shear_x * yt) / determinante
                y_origem = (yt + shear_y * xt) / determinante

                origem_j = int(round(x_origem))
                origem_i = int(round(y_origem))

                if 0 <= origem_i < altura and 0 <= origem_j < largura:
                    saida[i][j] = matriz[origem_i][origem_j]

        return saida

    '''
    Aplica o mapeamento tipo Gato de Arnold no maior quadrado N x N valido.

    Como calcula:
    - Define N como a menor dimensao da imagem.
    - Usa fator_x como numero de iteracoes (>= 1).
    - Em cada iteracao aplica mapeamento inverso dentro de N x N.
    - Fora da regiao N x N, preserva os pixels originais.
    '''
    def cisalhamento_arnold(self, matriz, fator_x=1.0, valor_fundo=0):
        self.validar_matriz(matriz)

        altura = len(matriz)
        largura = len(matriz[0])
        n = min(altura, largura)
        iteracoes = max(1, int(fator_x))

        imagem_atual = [linha[:] for linha in matriz]

        for _ in range(iteracoes):
            nova_imagem = self.criar_matriz(altura, largura, valor_fundo)

            for i in range(altura):
                for j in range(largura):
                    if i < n and j < n:
                        origem_j = (2 * j - i) % n
                        origem_i = (-j + i) % n
                        nova_imagem[i][j] = imagem_atual[origem_i][origem_j]
                    else:
                        nova_imagem[i][j] = imagem_atual[i][j]

            imagem_atual = [linha[:] for linha in nova_imagem]

        return imagem_atual
