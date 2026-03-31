from operacoes.base_operacoes import BaseOperacoesImagem

class BaseMorfologiaImagem(BaseOperacoesImagem):
    '''
    Converte imagem em tons de cinza para binaria.
    '''
    def binarizar(self, matriz, limiar=127):
        self.validar_matriz(matriz)
        # PASSO 1: Percorre todas as linhas e pixels da matriz.
        # PASSO 2: Aplica a regra do limiar (threshold). Se for maior ou igual ao corte, vira branco (255), senão preto (0).
        return [
            [255 if pixel >= limiar else 0 for pixel in linha]
            for linha in matriz
        ]

    '''
    Gera elemento estruturante quadrado.
    '''
    def gerar_elemento_estruturante_quadrado(self, tamanho=3):
        tamanho = int(tamanho)
        if tamanho not in (3, 5):
            raise ValueError("Use apenas elemento estruturante quadrado 3x3 ou 5x5.")

        # PASSO 1: Retorna uma matriz quadrada preenchida inteiramente com 1s (ativos).
        return [[1] * tamanho for _ in range(tamanho)]

    '''
    Interpreta texto do elemento estruturante.
    '''
    def parsear_elemento_estruturante(self, texto):
        # PASSO 1: Limpa a string de entrada, removendo colchetes e espaços extras.
        texto = (texto or "").replace("[", " ").replace("]", " ").strip()
        if not texto:
            texto = "1 1 1; 1 +1 1; 1 1 1" # Padrão caso venha vazio

        # PASSO 2: Separa as linhas do elemento usando o ponto e vírgula ou quebras de linha.
        linhas_brutas = []
        if ";" in texto:
            linhas_brutas = [parte.strip() for parte in texto.split(";")]
        else:
            linhas_brutas = [parte.strip() for parte in texto.splitlines()]

        linhas = [linha for linha in linhas_brutas if linha]
        if not linhas:
            raise ValueError("Elemento estruturante invalido.")
        if len(linhas) > 5:
            raise ValueError("Elemento estruturante deve ter no maximo 5 linhas.")

        mascara = []
        origem = None
        largura = None

        # PASSO 3: Analisa cada linha e cada caractere (token) para montar a matriz binária.
        for i, linha in enumerate(linhas):
            tokens = linha.replace(",", " ").split()
            if not tokens:
                continue

            if len(tokens) > 5:
                raise ValueError("Elemento estruturante deve ter no maximo 5 colunas.")

            # Garante que a matriz seja retangular perfeita.
            if largura is None:
                largura = len(tokens)
            elif len(tokens) != largura:
                raise ValueError("Todas as linhas do elemento estruturante devem ter o mesmo tamanho.")

            linha_mascara = []
            for j, token in enumerate(tokens):
                # Se achar o '+1', define essa coordenada como o centro (origem) do carimbo.
                if token == "+1":
                    if origem is not None:
                        raise ValueError("Use apenas um +1 para definir a origem.")
                    origem = (i, j)
                    linha_mascara.append(1)
                elif token in {"1", "0"}:
                    linha_mascara.append(int(token))
                else:
                    raise ValueError("Use apenas 0, 1 e +1 no elemento estruturante.")

            mascara.append(linha_mascara)

        if not mascara or not mascara[0]:
            raise ValueError("Elemento estruturante invalido.")
        if not any(valor == 1 for linha in mascara for valor in linha):
            raise ValueError("Elemento estruturante precisa ter ao menos um valor ativo (1).")

        # Se o usuário não definiu a origem com '+1', assume o centro geométrico da matriz.
        if origem is None:
            origem = (len(mascara) // 2, len(mascara[0]) // 2)

        return mascara, origem

    '''
    Normaliza elemento estruturante textual ou matricial.
    '''
    def _normalizar_elemento_estruturante(self, elemento_estruturante):
        # Se for texto, repassa para a função de parser textual logo acima.
        if isinstance(elemento_estruturante, str):
            return self.parsear_elemento_estruturante(elemento_estruturante)

        # PASSO 1: Se for matriz nativa (lista de listas), faz a validação de segurança.
        if not elemento_estruturante or not elemento_estruturante[0]:
            raise ValueError("Elemento estruturante invalido.")
        if len(elemento_estruturante) > 5 or len(elemento_estruturante[0]) > 5:
            raise ValueError("Elemento estruturante deve ter no maximo 5x5.")

        largura = len(elemento_estruturante[0])
        mascara = []
        origem = None

        # PASSO 2: Converte os valores booleanos/inteiros/strings da matriz de entrada para 0s e 1s rígidos.
        for i, linha in enumerate(elemento_estruturante):
            if len(linha) != largura:
                raise ValueError("Elemento estruturante invalido: linhas com tamanhos diferentes.")

            linha_mascara = []
            for j, valor in enumerate(linha):
                if valor == "+1":
                    if origem is not None:
                        raise ValueError("Use apenas um +1 para definir a origem.")
                    origem = (i, j)
                    linha_mascara.append(1)
                elif valor in (1, "1", True):
                    linha_mascara.append(1)
                elif valor in (0, "0", False):
                    linha_mascara.append(0)
                else:
                    raise ValueError("Use apenas valores 0/1 no elemento estruturante.")

            mascara.append(linha_mascara)

        if not any(valor == 1 for linha in mascara for valor in linha):
            raise ValueError("Elemento estruturante precisa ter ao menos um valor ativo (1).")

        if origem is None:
            origem = (len(mascara) // 2, len(mascara[0]) // 2)

        return mascara, origem

    '''
    Aplica operacao local usando o elemento estruturante.
    '''
    def _aplicar_elemento(self, matriz, elemento_estruturante, valor_borda, callback_vizinhanca):
        self.validar_matriz(matriz)

        altura = len(matriz)
        largura = len(matriz[0])
        mascara, origem = self._normalizar_elemento_estruturante(elemento_estruturante)
        eh = len(mascara)
        ew = len(mascara[0])
        oi, oj = origem
        saida = self.criar_matriz(altura, largura, 0)

        # PASSO 1: Percorre toda a imagem pixel por pixel.
        for i in range(altura):
            for j in range(largura):
                vizinhos = []
                
                # PASSO 2: Posiciona o elemento estruturante (carimbo) com a origem sobre o pixel atual (i, j).
                # Percorre as células internas do carimbo.
                for ei in range(eh):
                    for ej in range(ew):
                        # Ignora as partes do carimbo que são zero (não afetam a imagem).
                        if mascara[ei][ej] == 0:
                            continue

                        # PASSO 3: Calcula a coordenada real na imagem que está debaixo da parte ativa do carimbo.
                        ii = i + (ei - oi)
                        jj = j + (ej - oj)
                        
                        # PASSO 4: Pega o valor da imagem nesse ponto (lidando com as bordas) e guarda na lista de vizinhos.
                        valor = self.obter_pixel_com_fundo(matriz, ii, jj, valor_borda)
                        vizinhos.append(valor)

                # PASSO 5: Envia todos os pixels que o carimbo tocou para uma função de decisão (callback).
                # Ex: Se for dilatação, o callback acha o máximo. Se for erosão, acha o mínimo.
                saida[i][j] = callback_vizinhanca(vizinhos)

        return saida

    def _subtrair_matrizes(self, matriz_a, matriz_b):
        # Subtrai A de B pixel a pixel, mantendo o resultado puro (sem travar entre 0 e 255 ainda).
        return self.aplicar_entre_imagens(
            matriz_a,
            matriz_b,
            lambda a, b, _i, _j: self.limitar(a - b),
            limitar_saida=False,
        )


class MorfologiaBinariaImagem(BaseMorfologiaImagem):
    _SELETORES_3X3 = {"quadrado 3x3", "3x3", "3"}
    _SELETORES_5X5 = {"quadrado 5x5", "5x5", "5"}
    
    MASCARAS_HIT_OR_MISS = {
        "Ponto isolado": [
            [0, 0, 0],
            [0, 1, 0],
            [0, 0, 0],
        ],
        "Canto superior esquerdo": [
            [1, 1, -1],
            [1, 0, -1],
            [-1, -1, -1],
        ],
        "Cruz": [
            [0, 1, 0],
            [1, 1, 1],
            [0, 1, 0],
        ],
    }

    def _normalizar_entrada_binaria(self, matriz):
        self.validar_matriz(matriz)
        return self.binarizar(matriz)

    def _resolver_elemento_binario(self, elemento_estruturante):
        if isinstance(elemento_estruturante, str):
            seletor = " ".join(elemento_estruturante.strip().lower().split())
            if seletor in self._SELETORES_3X3:
                return self.gerar_elemento_estruturante_quadrado(3)
            if seletor in self._SELETORES_5X5:
                return self.gerar_elemento_estruturante_quadrado(5)

        return elemento_estruturante

    def _matriz_binaria_para_conjunto(self, matriz_binaria):
        self.validar_matriz(matriz_binaria)
        ativos = set()
        
        # PASSO 1: Transforma a matriz cheia de zeros em um conjunto (set) apenas com as coordenadas (i,j) 
        # onde a imagem tem cor branca (ativa). Isso torna as operações matemáticas de conjunto extremamente rápidas.
        for i, linha in enumerate(matriz_binaria):
            for j, valor in enumerate(linha):
                if valor != 0:
                    ativos.add((i, j))

        return ativos

    def _conjunto_para_matriz_binaria(self, conjunto, altura, largura):
        saida = self.criar_matriz(altura, largura, 0)
        
        # PASSO 1: Pega a lista de coordenadas resultantes da operação matemática
        # e "pinta" a matriz vazia com a cor branca (255) nos locais corretos.
        for i, j in conjunto:
            if 0 <= i < altura and 0 <= j < largura:
                saida[i][j] = 255
        return saida

    def _obter_offsets_elemento_estruturante(self, elemento_estruturante):
        # PASSO 1: Padroniza o carimbo sem escalar pelo tamanho da imagem.
        elemento_normalizado = self._resolver_elemento_binario(elemento_estruturante)
        mascara, origem = self._normalizar_elemento_estruturante(elemento_normalizado)
        oi, oj = origem

        offsets = set()
        
        # PASSO 2: Converte a matriz do carimbo em distâncias de coordenadas (offsets).
        # Ex: "Este ponto ativo está 1 pixel acima e 1 à direita do centro".
        for ei in range(len(mascara)):
            for ej in range(len(mascara[0])):
                if mascara[ei][ej] == 1:
                    offsets.add((ei - oi, ej - oj))

        return offsets

    def _normalizar_mascara_hit_or_miss(self, mascara):
        if isinstance(mascara, str):
            mascara = self.MASCARAS_HIT_OR_MISS.get(mascara, None)

        if not mascara or not mascara[0]:
            raise ValueError("Mascara hit-or-miss invalida.")

        largura = len(mascara[0])
        resultado = []
        for linha in mascara:
            if len(linha) != largura:
                raise ValueError("Mascara hit-or-miss invalida: linhas com tamanhos diferentes.")

            linha_normalizada = []
            for valor in linha:
                if valor in (1, "1", True):
                    linha_normalizada.append(1)
                elif valor in (0, "0", False):
                    linha_normalizada.append(0)
                elif valor in (-1, "-1"):
                    linha_normalizada.append(-1)
                else:
                    raise ValueError("Mascara hit-or-miss aceita apenas 1, 0 e -1.")
            resultado.append(linha_normalizada)

        if not any(valor == 1 for linha in resultado for valor in linha):
            raise ValueError("Mascara hit-or-miss precisa ter ao menos um pixel de frente (1).")

        return resultado

    def _dilatacao_binaria_conjunto(self, conjunto_a, offsets_b, altura, largura):
        resultado = set()

        # PASSO 1: Para cada pixel branco na imagem (conjunto_a)...
        for ai, aj in conjunto_a:
            # ... pega todas as posições do carimbo (offsets_b) ...
            for bi, bj in offsets_b:
                ni = ai + bi
                nj = aj + bj
                # ... e "acende" os vizinhos pintando-os (adicionando ao conjunto de resultado).
                if 0 <= ni < altura and 0 <= nj < largura:
                    resultado.add((ni, nj))

        return resultado

    def _erosao_binaria_conjunto(self, conjunto_a, offsets_b, altura, largura):
        resultado = set()

        # PASSO 1: Varre a imagem inteira (candidatos a continuarem brancos).
        for i in range(altura):
            for j in range(largura):
                contem_todos = True

                # PASSO 2: Posição por posição do carimbo, verifica se ele "cabe" na área branca.
                for bi, bj in offsets_b:
                    ni = i + bi
                    nj = j + bj

                    # Se a borda do carimbo cair fora da imagem, não cabe perfeitamente.
                    if not (0 <= ni < altura and 0 <= nj < largura):
                        contem_todos = False
                        break

                    # Se qualquer ponta do carimbo cair no fundo preto da imagem, não cabe.
                    if (ni, nj) not in conjunto_a:
                        contem_todos = False
                        break

                # PASSO 3: Só mantém o pixel como branco se o carimbo inteiro coube no fundo branco original.
                if contem_todos:
                    resultado.add((i, j))

        return resultado

    # =========================================================================
    # Os métodos públicos abaixo apenas convertem as matrizes, chamam a 
    # lógica de conjuntos que comentamos acima, e devolvem a matriz final.
    # =========================================================================

    def dilatacao_binaria(self, matriz_binaria, elemento_estruturante):
        matriz_normalizada = self._normalizar_entrada_binaria(matriz_binaria)
        altura, largura = len(matriz_normalizada), len(matriz_normalizada[0])

        # 1: Pega os pixels da imagem.
        conjunto_a = self._matriz_binaria_para_conjunto(matriz_normalizada)
        # 2: Pega a estrutura do carimbo.
        offsets_b = self._obter_offsets_elemento_estruturante(elemento_estruturante)
        # 3: Aplica a dilatação (expande a forma).
        resultado = self._dilatacao_binaria_conjunto(conjunto_a, offsets_b, altura, largura)
        # 4: Devolve a matriz pronta.
        return self._conjunto_para_matriz_binaria(resultado, altura, largura)

    def erosao_binaria(self, matriz_binaria, elemento_estruturante):
        matriz_normalizada = self._normalizar_entrada_binaria(matriz_binaria)
        altura, largura = len(matriz_normalizada), len(matriz_normalizada[0])

        conjunto_a = self._matriz_binaria_para_conjunto(matriz_normalizada)
        offsets_b = self._obter_offsets_elemento_estruturante(elemento_estruturante)

        # Aplica a erosão (retrai a forma).
        resultado = self._erosao_binaria_conjunto(conjunto_a, offsets_b, altura, largura)
        return self._conjunto_para_matriz_binaria(resultado, altura, largura)

    def abertura_binaria(self, matriz_binaria, elemento_estruturante):
        matriz_normalizada = self._normalizar_entrada_binaria(matriz_binaria)
        erodida = self.erosao_binaria(matriz_normalizada, elemento_estruturante)
        return self.dilatacao_binaria(erodida, elemento_estruturante)

    def fechamento_binaria(self, matriz_binaria, elemento_estruturante):
        matriz_normalizada = self._normalizar_entrada_binaria(matriz_binaria)
        dilatada = self.dilatacao_binaria(matriz_normalizada, elemento_estruturante)
        return self.erosao_binaria(dilatada, elemento_estruturante)

    def gradiente_binaria(self, matriz_binaria, elemento_estruturante):
        matriz_normalizada = self._normalizar_entrada_binaria(matriz_binaria)
        dilatada = self.dilatacao_binaria(matriz_normalizada, elemento_estruturante)
        erodida = self.erosao_binaria(matriz_normalizada, elemento_estruturante)
        return self._subtrair_matrizes(dilatada, erodida)

    def contorno_externo_binaria(self, matriz_binaria, elemento_estruturante):
        matriz_normalizada = self._normalizar_entrada_binaria(matriz_binaria)
        dilatada = self.dilatacao_binaria(matriz_normalizada, elemento_estruturante)
        return self._subtrair_matrizes(dilatada, matriz_normalizada)

    def contorno_interno_binaria(self, matriz_binaria, elemento_estruturante):
        matriz_normalizada = self._normalizar_entrada_binaria(matriz_binaria)
        erodida = self.erosao_binaria(matriz_normalizada, elemento_estruturante)
        return self._subtrair_matrizes(matriz_normalizada, erodida)

    def top_hat_binaria(self, matriz_binaria, elemento_estruturante):
        matriz_normalizada = self._normalizar_entrada_binaria(matriz_binaria)
        aberta = self.abertura_binaria(matriz_normalizada, elemento_estruturante)
        return self._subtrair_matrizes(matriz_normalizada, aberta)

    def bottom_hat_binaria(self, matriz_binaria, elemento_estruturante):
        matriz_normalizada = self._normalizar_entrada_binaria(matriz_binaria)
        fechada = self.fechamento_binaria(matriz_normalizada, elemento_estruturante)
        return self._subtrair_matrizes(fechada, matriz_normalizada)

    def hit_or_miss_binaria(self, matriz_binaria, mascara):
        matriz_normalizada = self._normalizar_entrada_binaria(matriz_binaria)
        altura = len(matriz_normalizada)
        largura = len(matriz_normalizada[0])
        mascara_normalizada = self._normalizar_mascara_hit_or_miss(mascara)

        mh = len(mascara_normalizada)
        mw = len(mascara_normalizada[0])
        oi = mh // 2
        oj = mw // 2

        saida = self.criar_matriz(altura, largura, 0)
        for i in range(altura):
            for j in range(largura):
                corresponde = True
                for mi in range(mh):
                    for mj in range(mw):
                        esperado = mascara_normalizada[mi][mj]
                        if esperado == -1:
                            continue

                        ii = i + (mi - oi)
                        jj = j + (mj - oj)
                        valor = self.obter_pixel_com_fundo(matriz_normalizada, ii, jj, 0)
                        ativo = valor != 0

                        if esperado == 1 and not ativo:
                            corresponde = False
                            break
                        if esperado == 0 and ativo:
                            corresponde = False
                            break
                    if not corresponde:
                        break

                if corresponde:
                    saida[i][j] = 255

        return saida


class MorfologiaCinzaImagem(BaseMorfologiaImagem):
    RAIO_ELEMENTO_CIRCULAR = 2

    def _gerar_elemento_estruturante_circular_flat(self):
        raio = self.RAIO_ELEMENTO_CIRCULAR
        tamanho = 2 * raio + 1
        centro = raio
        mascara = []
        
        # PASSO 1: Usa a equação da circunferência para definir quais pixels da matriz pertencem ao círculo.
        for i in range(tamanho):
            linha = []
            for j in range(tamanho):
                # Se a distância do pixel até o centro for menor/igual ao raio, ativa ele (1).
                dentro = (i - centro) * (i - centro) + (j - centro) * (j - centro) <= raio * raio
                linha.append(1 if dentro else 0)
            mascara.append(linha)

        return mascara, (centro, centro)

    def obter_texto_elemento_estruturante(self):
        mascara, (ci, cj) = self._gerar_elemento_estruturante_circular_flat()
        linhas = []
        
        # PASSO 1: Converte o círculo numérico para string, marcando o pixel do meio com "+1" para inspeção visual.
        for i, linha in enumerate(mascara):
            valores = []
            for j, valor in enumerate(linha):
                if i == ci and j == cj and valor == 1:
                    valores.append("+1")
                else:
                    valores.append(str(valor))
            linhas.append(" ".join(valores))
        return "\n".join(linhas)

    def _resolver_elemento_estruturante_cinza(self, elemento_estruturante):
        if elemento_estruturante is None:
            return self.obter_texto_elemento_estruturante()

        if isinstance(elemento_estruturante, str) and not elemento_estruturante.strip():
            return self.obter_texto_elemento_estruturante()

        return elemento_estruturante

    def _aplicar_elemento_cinza(self, matriz, valor_borda, callback_vizinhanca, elemento_estruturante=None):
        elemento = self._resolver_elemento_estruturante_cinza(elemento_estruturante)
        return self._aplicar_elemento(matriz, elemento, valor_borda, callback_vizinhanca)

    # =========================================================================
    # Operações em Tom de Cinza
    # O conceito se repete, mas ao invés de usar teoria dos conjuntos como 
    # na binária, ele utiliza a matemática do mínimo e do máximo local.
    # =========================================================================

    def dilatacao_cinza(self, matriz, elemento_estruturante=None):
        # Passo Único: Pega o vizinho com o tom MAIS CLARO (max) tocado pelo carimbo.
        return self._aplicar_elemento_cinza(
            matriz,
            0,
            lambda vizinhos: max(vizinhos),
            elemento_estruturante,
        )

    def erosao_cinza(self, matriz, elemento_estruturante=None):
        # Passo Único: Pega o vizinho com o tom MAIS ESCURO (min) tocado pelo carimbo.
        return self._aplicar_elemento_cinza(
            matriz,
            255,
            lambda vizinhos: min(vizinhos),
            elemento_estruturante,
        )

    def abertura_cinza(self, matriz, elemento_estruturante=None):
        # Erode (escurece pontos finos claros) e depois Dilata (restaura o restante).
        erodida = self.erosao_cinza(matriz, elemento_estruturante)
        return self.dilatacao_cinza(erodida, elemento_estruturante)

    def fechamento_cinza(self, matriz, elemento_estruturante=None):
        # Dilata (engole pontos finos escuros) e depois Erode (restaura o restante).
        dilatada = self.dilatacao_cinza(matriz, elemento_estruturante)
        return self.erosao_cinza(dilatada, elemento_estruturante)

    def gradiente_cinza(self, matriz, elemento_estruturante=None):
        # Subtrai a versão mais escura (erodida) da mais clara (dilatada) para destacar as bordas visíveis.
        dilatada = self.dilatacao_cinza(matriz, elemento_estruturante)
        erodida = self.erosao_cinza(matriz, elemento_estruturante)
        return self._subtrair_matrizes(dilatada, erodida)

    def contorno_externo_cinza(self, matriz, elemento_estruturante=None):
        dilatada = self.dilatacao_cinza(matriz, elemento_estruturante)
        return self._subtrair_matrizes(dilatada, matriz)

    def contorno_interno_cinza(self, matriz, elemento_estruturante=None):
        erodida = self.erosao_cinza(matriz, elemento_estruturante)
        return self._subtrair_matrizes(matriz, erodida)

    def top_hat_cinza(self, matriz, elemento_estruturante=None):
        # Pega a imagem original e recorta (subtrai) a Abertura, deixando apenas 
        # as manchas claras ou reflexos finos que a abertura cortou.
        aberta = self.abertura_cinza(matriz, elemento_estruturante)
        return self._subtrair_matrizes(matriz, aberta)

    def bottom_hat_cinza(self, matriz, elemento_estruturante=None):
        # Pega o Fechamento e corta (subtrai) a imagem original, isolando e 
        # realçando os pequenos detalhes pretos que estavam no fundo claro.
        fechada = self.fechamento_cinza(matriz, elemento_estruturante)
        return self._subtrair_matrizes(fechada, matriz)