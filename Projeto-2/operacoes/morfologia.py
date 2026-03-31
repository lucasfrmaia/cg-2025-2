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
    def gerar_elemento_estruturante_quadrado(self, tamanho=20):
        if tamanho < 1:
            raise ValueError("tamanho deve ser >= 1")

        # PASSO 1: Garante que o tamanho seja ímpar para que o elemento tenha um centro exato.
        if tamanho % 2 == 0:
            tamanho += 1

        # PASSO 2: Limita o tamanho para evitar processamento excessivo em operações básicas.
        if tamanho > 3:
            raise ValueError("O elemento estruturante deve ter no maximo 3x3.")

        # PASSO 3: Retorna uma matriz quadrada preenchida inteiramente com 1s (ativos).
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
        if len(linhas) > 3:
            raise ValueError("Elemento estruturante deve ter no maximo 3 linhas.")

        mascara = []
        origem = None
        largura = None

        # PASSO 3: Analisa cada linha e cada caractere (token) para montar a matriz binária.
        for i, linha in enumerate(linhas):
            tokens = linha.replace(",", " ").split()
            if not tokens:
                continue

            if len(tokens) > 3:
                raise ValueError("Elemento estruturante deve ter no maximo 3 colunas.")

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
        if len(elemento_estruturante) > 3 or len(elemento_estruturante[0]) > 3:
            raise ValueError("Elemento estruturante deve ter no maximo 3x3.")

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

    def _calcular_fator_escala_elemento(self, altura, largura):
        # PASSO 1: Calcula o quão grande é a imagem e escala o elemento estruturante.
        # Imagens maiores precisam de carimbos maiores para o efeito ser visível.
        menor_dimensao = min(altura, largura)
        fator = max(1, menor_dimensao // 80)
        return min(7, fator)

    def _escalar_mascara_elemento(self, mascara, origem, fator):
        if fator == 1:
            return mascara, origem

        # PASSO 1: Multiplica o tamanho da matriz do carimbo pelo fator de escala.
        altura_base = len(mascara)
        largura_base = len(mascara[0])
        altura_esc = altura_base * fator
        largura_esc = largura_base * fator
        mascara_esc = [[0] * largura_esc for _ in range(altura_esc)]

        # PASSO 2: Preenche os "blocos" na nova matriz para manter o formato original do carimbo, só que maior.
        for i in range(altura_base):
            for j in range(largura_base):
                if mascara[i][j] == 0:
                    continue

                inicio_i = i * fator
                inicio_j = j * fator
                
                for ii in range(inicio_i, inicio_i + fator):
                    for jj in range(inicio_j, inicio_j + fator):
                        mascara_esc[ii][jj] = 1

        # PASSO 3: Recalcula onde fica o centro do carimbo na nova matriz esticada.
        oi, oj = origem
        origem_esc = (oi * fator + (fator // 2), oj * fator + (fator // 2))

        return mascara_esc, origem_esc

    def _obter_offsets_elemento_estruturante(self, elemento_estruturante, altura, largura):
        # PASSO 1: Padroniza o carimbo e ajusta o tamanho (escala).
        mascara, origem = self._normalizar_elemento_estruturante(elemento_estruturante)
        fator_escala = self._calcular_fator_escala_elemento(altura, largura)
        mascara, origem = self._escalar_mascara_elemento(mascara, origem, fator_escala)
        oi, oj = origem

        offsets = set()
        
        # PASSO 2: Converte a matriz do carimbo em distâncias de coordenadas (offsets).
        # Ex: "Este ponto ativo está 1 pixel acima e 1 à direita do centro".
        for ei in range(len(mascara)):
            for ej in range(len(mascara[0])):
                if mascara[ei][ej] == 1:
                    offsets.add((ei - oi, ej - oj))

        return offsets

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
        self.validar_matriz(matriz_binaria)
        altura, largura = len(matriz_binaria), len(matriz_binaria[0])
        
        # 1: Pega os pixels da imagem.
        conjunto_a = self._matriz_binaria_para_conjunto(matriz_binaria)
        # 2: Pega a estrutura do carimbo.
        offsets_b = self._obter_offsets_elemento_estruturante(elemento_estruturante, altura, largura)
        # 3: Aplica a dilatação (expande a forma).
        resultado = self._dilatacao_binaria_conjunto(conjunto_a, offsets_b, altura, largura)
        # 4: Devolve a matriz pronta.
        return self._conjunto_para_matriz_binaria(resultado, altura, largura)

    def erosao_binaria(self, matriz_binaria, elemento_estruturante):
        self.validar_matriz(matriz_binaria)
        altura, largura = len(matriz_binaria), len(matriz_binaria[0])

        conjunto_a = self._matriz_binaria_para_conjunto(matriz_binaria)
        offsets_b = self._obter_offsets_elemento_estruturante(elemento_estruturante, altura, largura)

        # Aplica a erosão (retrai a forma).
        resultado = self._erosao_binaria_conjunto(conjunto_a, offsets_b, altura, largura)
        return self._conjunto_para_matriz_binaria(resultado, altura, largura)

    def abertura_binaria(self, matriz_binaria, elemento_estruturante):
        self.validar_matriz(matriz_binaria)
        altura, largura = len(matriz_binaria), len(matriz_binaria[0])

        conjunto_a = self._matriz_binaria_para_conjunto(matriz_binaria)
        offsets_b = self._obter_offsets_elemento_estruturante(elemento_estruturante, altura, largura)

        # Passo A: Erode para sumir com os ruídos finos.
        erodida = self._erosao_binaria_conjunto(conjunto_a, offsets_b, altura, largura)
        # Passo B: Dilata o que sobreviveu para restaurar o tamanho da forma.
        aberta = self._dilatacao_binaria_conjunto(erodida, offsets_b, altura, largura)
        
        return self._conjunto_para_matriz_binaria(aberta, altura, largura)

    def fechamento_binaria(self, matriz_binaria, elemento_estruturante):
        self.validar_matriz(matriz_binaria)
        altura, largura = len(matriz_binaria), len(matriz_binaria[0])

        conjunto_a = self._matriz_binaria_para_conjunto(matriz_binaria)
        offsets_b = self._obter_offsets_elemento_estruturante(elemento_estruturante, altura, largura)

        # Passo A: Dilata para tampar buracos e fendas na forma.
        dilatada = self._dilatacao_binaria_conjunto(conjunto_a, offsets_b, altura, largura)
        # Passo B: Erode para desinchar as bordas do objeto de volta ao normal.
        fechada = self._erosao_binaria_conjunto(dilatada, offsets_b, altura, largura)
        
        return self._conjunto_para_matriz_binaria(fechada, altura, largura)

    def gradiente_binaria(self, matriz_binaria, elemento_estruturante):
        self.validar_matriz(matriz_binaria)
        altura, largura = len(matriz_binaria), len(matriz_binaria[0])

        conjunto_a = self._matriz_binaria_para_conjunto(matriz_binaria)
        offsets_b = self._obter_offsets_elemento_estruturante(elemento_estruturante, altura, largura)

        # Passo A: Dilata a forma (imagem incha).
        dilatada = self._dilatacao_binaria_conjunto(conjunto_a, offsets_b, altura, largura)
        # Passo B: Erode a forma (imagem murcha).
        erodida = self._erosao_binaria_conjunto(conjunto_a, offsets_b, altura, largura)
        
        # Passo C: Subtrai a forma murcha da forma inchada. O que sobra no meio é apenas a borda do objeto.
        gradiente = dilatada.difference(erodida)
        return self._conjunto_para_matriz_binaria(gradiente, altura, largura)

    def contorno_externo_binaria(self, matriz_binaria, elemento_estruturante):
        self.validar_matriz(matriz_binaria)
        altura, largura = len(matriz_binaria), len(matriz_binaria[0])

        conjunto_a = self._matriz_binaria_para_conjunto(matriz_binaria)
        offsets_b = self._obter_offsets_elemento_estruturante(elemento_estruturante, altura, largura)

        # Dilata a forma e subtrai a forma original (sobra a borda que cresceu para fora).
        dilatada = self._dilatacao_binaria_conjunto(conjunto_a, offsets_b, altura, largura)
        contorno = dilatada.difference(conjunto_a)
        
        return self._conjunto_para_matriz_binaria(contorno, altura, largura)

    def contorno_interno_binaria(self, matriz_binaria, elemento_estruturante):
        self.validar_matriz(matriz_binaria)
        altura, largura = len(matriz_binaria), len(matriz_binaria[0])

        conjunto_a = self._matriz_binaria_para_conjunto(matriz_binaria)
        offsets_b = self._obter_offsets_elemento_estruturante(elemento_estruturante, altura, largura)

        # Erode a forma e subtrai a forma murcha da original (sobra a borda de dentro que foi engolida).
        erodida = self._erosao_binaria_conjunto(conjunto_a, offsets_b, altura, largura)
        contorno = conjunto_a.difference(erodida)
        
        return self._conjunto_para_matriz_binaria(contorno, altura, largura)


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

    def obter_texto_elemento_estruturante_circular_flat(self):
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

    def _aplicar_elemento_circular_flat(self, matriz, valor_borda, callback_vizinhanca):
        self.validar_matriz(matriz)
        altura = len(matriz)
        largura = len(matriz[0])
        mascara, origem = self._gerar_elemento_estruturante_circular_flat()
        eh, ew = len(mascara), len(mascara[0])
        oi, oj = origem
        saida = self.criar_matriz(altura, largura, 0)

        # PASSO 1: Percorre a imagem.
        for i in range(altura):
            for j in range(largura):
                vizinhos = []
                # PASSO 2: Posiciona o disco circular em cima do pixel. Pega todos os valores de brilho que o disco toca.
                for ei in range(eh):
                    for ej in range(ew):
                        if mascara[ei][ej] == 0:
                            continue

                        ii = i + (ei - oi)
                        jj = j + (ej - oj)
                        valor = self.obter_pixel_com_fundo(matriz, ii, jj, valor_borda)
                        vizinhos.append(valor)

                # PASSO 3: Entrega a lista de intensidades capturadas para o callback extrair o maior ou o menor valor.
                saida[i][j] = callback_vizinhanca(vizinhos)

        return saida

    # =========================================================================
    # Operações em Tom de Cinza
    # O conceito se repete, mas ao invés de usar teoria dos conjuntos como 
    # na binária, ele utiliza a matemática do mínimo e do máximo local.
    # =========================================================================

    def dilatacao_cinza(self, matriz, _elemento_estruturante=None):
        # Passo Único: Pega o vizinho com o tom MAIS CLARO (max) tocado pelo carimbo.
        return self._aplicar_elemento_circular_flat(matriz, 0, lambda vizinhos: max(vizinhos))

    def erosao_cinza(self, matriz, _elemento_estruturante=None):
        # Passo Único: Pega o vizinho com o tom MAIS ESCURO (min) tocado pelo carimbo.
        return self._aplicar_elemento_circular_flat(matriz, 255, lambda vizinhos: min(vizinhos))

    def abertura_cinza(self, matriz, _elemento_estruturante=None):
        # Erode (escurece pontos finos claros) e depois Dilata (restaura o restante).
        erodida = self.erosao_cinza(matriz)
        return self.dilatacao_cinza(erodida)

    def fechamento_cinza(self, matriz, _elemento_estruturante=None):
        # Dilata (engole pontos finos escuros) e depois Erode (restaura o restante).
        dilatada = self.dilatacao_cinza(matriz)
        return self.erosao_cinza(dilatada)

    def gradiente_cinza(self, matriz, _elemento_estruturante=None):
        # Subtrai a versão mais escura (erodida) da mais clara (dilatada) para destacar as bordas visíveis.
        dilatada = self.dilatacao_cinza(matriz)
        erodida = self.erosao_cinza(matriz)
        return self._subtrair_matrizes(dilatada, erodida)

    def contorno_externo_cinza(self, matriz, _elemento_estruturante=None):
        dilatada = self.dilatacao_cinza(matriz)
        return self._subtrair_matrizes(dilatada, matriz)

    def contorno_interno_cinza(self, matriz, _elemento_estruturante=None):
        erodida = self.erosao_cinza(matriz)
        return self._subtrair_matrizes(matriz, erodida)

    def top_hat_cinza(self, matriz, _elemento_estruturante=None):
        # Pega a imagem original e recorta (subtrai) a Abertura, deixando apenas 
        # as manchas claras ou reflexos finos que a abertura cortou.
        aberta = self.abertura_cinza(matriz)
        return self._subtrair_matrizes(matriz, aberta)

    def bottom_hat_cinza(self, matriz, _elemento_estruturante=None):
        # Pega o Fechamento e corta (subtrai) a imagem original, isolando e 
        # realçando os pequenos detalhes pretos que estavam no fundo claro.
        fechada = self.fechamento_cinza(matriz)
        return self._subtrair_matrizes(fechada, matriz)