from operacoes.base_operacoes import BaseOperacoesImagem


class BaseMorfologiaImagem(BaseOperacoesImagem):
    '''
    Converte imagem em tons de cinza para binaria.

    Como calcula:
    - Pixel >= limiar vira 255.
    - Pixel < limiar vira 0.
    '''
    def binarizar(self, matriz, limiar=127):
        self.validar_matriz(matriz)
        return [
            [255 if pixel >= limiar else 0 for pixel in linha]
            for linha in matriz
        ]

    '''
    Gera elemento estruturante quadrado.

    Como calcula:
    - Garante tamanho impar.
    - Restringe o tamanho ao maximo de 3x3.
    - Retorna matriz preenchida com 1.
    '''
    def gerar_elemento_estruturante_quadrado(self, tamanho=20):
        if tamanho < 1:
            raise ValueError("tamanho deve ser >= 1")

        if tamanho % 2 == 0:
            tamanho += 1

        if tamanho > 3:
            raise ValueError("O elemento estruturante deve ter no maximo 3x3.")

        return [[1] * tamanho for _ in range(tamanho)]

    '''
    Interpreta texto do elemento estruturante.

    Como calcula:
    - Separa linhas e colunas por delimitadores.
    - Converte tokens para mascara binaria.
    - Define origem por +1 ou pelo centro.
    '''
    def parsear_elemento_estruturante(self, texto):
        texto = (texto or "").replace("[", " ").replace("]", " ").strip()
        if not texto:
            texto = "1 1 1; 1 +1 1; 1 1 1"

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

        for i, linha in enumerate(linhas):
            tokens = linha.replace(",", " ").split()
            if not tokens:
                continue

            if len(tokens) > 3:
                raise ValueError("Elemento estruturante deve ter no maximo 3 colunas.")

            if largura is None:
                largura = len(tokens)
            elif len(tokens) != largura:
                raise ValueError("Todas as linhas do elemento estruturante devem ter o mesmo tamanho.")

            linha_mascara = []
            for j, token in enumerate(tokens):
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

        if origem is None:
            origem = (len(mascara) // 2, len(mascara[0]) // 2)

        return mascara, origem

    '''
    Normaliza elemento estruturante textual ou matricial.

    Como calcula:
    - Se for string, usa o parser textual.
    - Se for lista, valida formato e detecta origem.
    '''
    def _normalizar_elemento_estruturante(self, elemento_estruturante):
        if isinstance(elemento_estruturante, str):
            return self.parsear_elemento_estruturante(elemento_estruturante)

        if not elemento_estruturante or not elemento_estruturante[0]:
            raise ValueError("Elemento estruturante invalido.")

        if len(elemento_estruturante) > 3 or len(elemento_estruturante[0]) > 3:
            raise ValueError("Elemento estruturante deve ter no maximo 3x3.")

        largura = len(elemento_estruturante[0])
        mascara = []
        origem = None

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

    Como calcula:
    - Coleta vizinhos onde a mascara eh ativa.
    - Aplica callback da vizinhanca para gerar o pixel de saida.
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

        for i in range(altura):
            for j in range(largura):
                vizinhos = []
                for ei in range(eh):
                    for ej in range(ew):
                        if mascara[ei][ej] == 0:
                            continue

                        ii = i + (ei - oi)
                        jj = j + (ej - oj)
                        valor = self.obter_pixel_com_fundo(matriz, ii, jj, valor_borda)
                        vizinhos.append(valor)

                saida[i][j] = callback_vizinhanca(vizinhos)

        return saida

    '''
    Subtrai duas matrizes pixel a pixel.

    Como calcula:
    - Para cada posicao, calcula a - b.
    - Mantem a operacao sem limitacao automatica no motor.
    '''
    def _subtrair_matrizes(self, matriz_a, matriz_b):
        return self.aplicar_entre_imagens(
            matriz_a,
            matriz_b,
            lambda a, b, _i, _j: self.limitar(a - b),
            limitar_saida=False,
        )


class MorfologiaBinariaImagem(BaseMorfologiaImagem):
    '''
    Converte matriz binaria para conjunto de coordenadas ativas.

    Como calcula:
    - Percorre os pixels.
    - Adiciona (i, j) quando o valor for diferente de zero.
    '''
    def _matriz_binaria_para_conjunto(self, matriz_binaria):
        self.validar_matriz(matriz_binaria)

        ativos = set()
        for i, linha in enumerate(matriz_binaria):
            for j, valor in enumerate(linha):
                if valor != 0:
                    ativos.add((i, j))

        return ativos

    '''
    Converte conjunto de coordenadas em matriz binaria.

    Como calcula:
    - Inicializa matriz com zeros.
    - Marca 255 nas coordenadas validas do conjunto.
    '''
    def _conjunto_para_matriz_binaria(self, conjunto, altura, largura):
        saida = self.criar_matriz(altura, largura, 0)
        for i, j in conjunto:
            if 0 <= i < altura and 0 <= j < largura:
                saida[i][j] = 255
        return saida

    '''
    Calcula fator de escala do elemento estruturante.

    Como calcula:
    - Usa a menor dimensao da imagem como referencia.
    - Limita o fator entre 1 e 7.
    '''
    def _calcular_fator_escala_elemento(self, altura, largura):
        menor_dimensao = min(altura, largura)
        fator = max(1, menor_dimensao // 80)
        return min(7, fator)

    '''
    Escala a mascara do elemento estruturante.

    Como calcula:
    - Replica cada celula ativa em blocos fator x fator.
    - Recalcula a origem no elemento escalado.
    '''
    def _escalar_mascara_elemento(self, mascara, origem, fator):
        if fator == 1:
            return mascara, origem

        altura_base = len(mascara)
        largura_base = len(mascara[0])
        altura_esc = altura_base * fator
        largura_esc = largura_base * fator
        mascara_esc = [[0] * largura_esc for _ in range(altura_esc)]

        for i in range(altura_base):
            for j in range(largura_base):
                if mascara[i][j] == 0:
                    continue

                inicio_i = i * fator
                inicio_j = j * fator
                for ii in range(inicio_i, inicio_i + fator):
                    for jj in range(inicio_j, inicio_j + fator):
                        mascara_esc[ii][jj] = 1

        oi, oj = origem
        origem_esc = (oi * fator + (fator // 2), oj * fator + (fator // 2))
        return mascara_esc, origem_esc

    '''
    Gera offsets dos pontos ativos do elemento estruturante.

    Como calcula:
    - Normaliza e escala o elemento.
    - Converte celulas ativas em deslocamentos relativos a origem.
    '''
    def _obter_offsets_elemento_estruturante(self, elemento_estruturante, altura, largura):
        mascara, origem = self._normalizar_elemento_estruturante(elemento_estruturante)
        fator_escala = self._calcular_fator_escala_elemento(altura, largura)
        mascara, origem = self._escalar_mascara_elemento(mascara, origem, fator_escala)
        oi, oj = origem

        offsets = set()
        for ei in range(len(mascara)):
            for ej in range(len(mascara[0])):
                if mascara[ei][ej] == 1:
                    offsets.add((ei - oi, ej - oj))

        return offsets

    '''
    Executa dilatacao binaria sobre conjuntos.

    Como calcula:
    - Para cada ponto ativo de A, soma todos os offsets de B.
    - Mantem somente coordenadas dentro da imagem.
    '''
    def _dilatacao_binaria_conjunto(self, conjunto_a, offsets_b, altura, largura):
        resultado = set()

        for ai, aj in conjunto_a:
            for bi, bj in offsets_b:
                ni = ai + bi
                nj = aj + bj
                if 0 <= ni < altura and 0 <= nj < largura:
                    resultado.add((ni, nj))

        return resultado

    '''
    Executa erosao binaria sobre conjuntos.

    Como calcula:
    - Testa cada pixel candidato.
    - Mantem o ponto apenas se todos os offsets estiverem ativos.
    '''
    def _erosao_binaria_conjunto(self, conjunto_a, offsets_b, altura, largura):
        resultado = set()

        for i in range(altura):
            for j in range(largura):
                contem_todos = True

                for bi, bj in offsets_b:
                    ni = i + bi
                    nj = j + bj

                    if not (0 <= ni < altura and 0 <= nj < largura):
                        contem_todos = False
                        break

                    if (ni, nj) not in conjunto_a:
                        contem_todos = False
                        break

                if contem_todos:
                    resultado.add((i, j))

        return resultado

    '''
    Realiza dilatacao binaria da imagem.

    Como calcula:
    - Converte matriz para conjunto de ativos.
    - Aplica dilatacao por offsets do elemento.
    - Converte o resultado de volta para matriz.
    '''
    def dilatacao_binaria(self, matriz_binaria, elemento_estruturante):
        self.validar_matriz(matriz_binaria)
        altura = len(matriz_binaria)
        largura = len(matriz_binaria[0])

        conjunto_a = self._matriz_binaria_para_conjunto(matriz_binaria)
        offsets_b = self._obter_offsets_elemento_estruturante(elemento_estruturante, altura, largura)

        resultado = self._dilatacao_binaria_conjunto(conjunto_a, offsets_b, altura, largura)
        
        return self._conjunto_para_matriz_binaria(resultado, altura, largura)

    '''
    Realiza erosao binaria da imagem.

    Como calcula:
    - Converte matriz para conjunto de ativos.
    - Aplica erosao por offsets do elemento.
    - Converte o resultado de volta para matriz.
    '''
    def erosao_binaria(self, matriz_binaria, elemento_estruturante):
        self.validar_matriz(matriz_binaria)
        altura = len(matriz_binaria)
        largura = len(matriz_binaria[0])

        conjunto_a = self._matriz_binaria_para_conjunto(matriz_binaria)
        offsets_b = self._obter_offsets_elemento_estruturante(elemento_estruturante, altura, largura)

        resultado = self._erosao_binaria_conjunto(conjunto_a, offsets_b, altura, largura)
        return self._conjunto_para_matriz_binaria(resultado, altura, largura)

    '''
    Realiza abertura binaria.

    Como calcula:
    - Aplica erosao.
    - Em seguida aplica dilatacao no resultado.
    '''
    def abertura_binaria(self, matriz_binaria, elemento_estruturante):
        self.validar_matriz(matriz_binaria)
        altura = len(matriz_binaria)
        largura = len(matriz_binaria[0])

        conjunto_a = self._matriz_binaria_para_conjunto(matriz_binaria)
        offsets_b = self._obter_offsets_elemento_estruturante(elemento_estruturante, altura, largura)

        erodida = self._erosao_binaria_conjunto(conjunto_a, offsets_b, altura, largura)
        aberta = self._dilatacao_binaria_conjunto(erodida, offsets_b, altura, largura)
        return self._conjunto_para_matriz_binaria(aberta, altura, largura)

    '''
    Realiza fechamento binario.

    Como calcula:
    - Aplica dilatacao.
    - Em seguida aplica erosao no resultado.
    '''
    def fechamento_binaria(self, matriz_binaria, elemento_estruturante):
        self.validar_matriz(matriz_binaria)
        altura = len(matriz_binaria)
        largura = len(matriz_binaria[0])

        conjunto_a = self._matriz_binaria_para_conjunto(matriz_binaria)
        offsets_b = self._obter_offsets_elemento_estruturante(elemento_estruturante, altura, largura)

        dilatada = self._dilatacao_binaria_conjunto(conjunto_a, offsets_b, altura, largura)
        fechada = self._erosao_binaria_conjunto(dilatada, offsets_b, altura, largura)
        return self._conjunto_para_matriz_binaria(fechada, altura, largura)

    '''
    Calcula gradiente morfologico binario.

    Como calcula:
    - Calcula dilatada e erodida.
    - Retorna diferenca de conjuntos: dilatada - erodida.
    '''
    def gradiente_binaria(self, matriz_binaria, elemento_estruturante):
        self.validar_matriz(matriz_binaria)
        altura = len(matriz_binaria)
        largura = len(matriz_binaria[0])

        conjunto_a = self._matriz_binaria_para_conjunto(matriz_binaria)
        offsets_b = self._obter_offsets_elemento_estruturante(elemento_estruturante, altura, largura)

        dilatada = self._dilatacao_binaria_conjunto(conjunto_a, offsets_b, altura, largura)
        erodida = self._erosao_binaria_conjunto(conjunto_a, offsets_b, altura, largura)
        gradiente = dilatada.difference(erodida)
        return self._conjunto_para_matriz_binaria(gradiente, altura, largura)

    '''
    Calcula contorno externo binario.

    Como calcula:
    - Calcula a imagem dilatada.
    - Retorna diferenca: dilatada - original.
    '''
    def contorno_externo_binaria(self, matriz_binaria, elemento_estruturante):
        self.validar_matriz(matriz_binaria)
        altura = len(matriz_binaria)
        largura = len(matriz_binaria[0])

        conjunto_a = self._matriz_binaria_para_conjunto(matriz_binaria)
        offsets_b = self._obter_offsets_elemento_estruturante(elemento_estruturante, altura, largura)

        dilatada = self._dilatacao_binaria_conjunto(conjunto_a, offsets_b, altura, largura)
        contorno = dilatada.difference(conjunto_a)
        return self._conjunto_para_matriz_binaria(contorno, altura, largura)

    '''
    Calcula contorno interno binario.

    Como calcula:
    - Calcula a imagem erodida.
    - Retorna diferenca: original - erodida.
    '''
    def contorno_interno_binaria(self, matriz_binaria, elemento_estruturante):
        self.validar_matriz(matriz_binaria)
        altura = len(matriz_binaria)
        largura = len(matriz_binaria[0])

        conjunto_a = self._matriz_binaria_para_conjunto(matriz_binaria)
        offsets_b = self._obter_offsets_elemento_estruturante(elemento_estruturante, altura, largura)

        erodida = self._erosao_binaria_conjunto(conjunto_a, offsets_b, altura, largura)
        contorno = conjunto_a.difference(erodida)
        return self._conjunto_para_matriz_binaria(contorno, altura, largura)


class MorfologiaCinzaImagem(BaseMorfologiaImagem):
    RAIO_ELEMENTO_CIRCULAR = 2

    '''
    Gera elemento estruturante circular flat em tons de cinza.

    Como calcula:
    - Usa equacao de circulo discreta para ativar celulas.
    - Define a origem no centro da mascara.
    '''
    def _gerar_elemento_estruturante_circular_flat(self):
        raio = self.RAIO_ELEMENTO_CIRCULAR
        tamanho = 2 * raio + 1
        centro = raio

        mascara = []
        for i in range(tamanho):
            linha = []
            for j in range(tamanho):
                dentro = (i - centro) * (i - centro) + (j - centro) * (j - centro) <= raio * raio
                linha.append(1 if dentro else 0)
            mascara.append(linha)

        return mascara, (centro, centro)

    '''
    Retorna versao textual do elemento circular flat.

    Como calcula:
    - Percorre a mascara circular.
    - Marca a origem com +1.
    '''
    def obter_texto_elemento_estruturante_circular_flat(self):
        mascara, (ci, cj) = self._gerar_elemento_estruturante_circular_flat()
        linhas = []
        for i, linha in enumerate(mascara):
            valores = []
            for j, valor in enumerate(linha):
                if i == ci and j == cj and valor == 1:
                    valores.append("+1")
                else:
                    valores.append(str(valor))
            linhas.append(" ".join(valores))
        return "\n".join(linhas)

    '''
    Aplica operador local com elemento circular flat.

    Como calcula:
    - Coleta vizinhos ativos da mascara circular.
    - Aplica callback para gerar pixel de saida.
    '''
    def _aplicar_elemento_circular_flat(self, matriz, valor_borda, callback_vizinhanca):
        self.validar_matriz(matriz)

        altura = len(matriz)
        largura = len(matriz[0])
        mascara, origem = self._gerar_elemento_estruturante_circular_flat()
        eh = len(mascara)
        ew = len(mascara[0])
        oi, oj = origem
        saida = self.criar_matriz(altura, largura, 0)

        for i in range(altura):
            for j in range(largura):
                vizinhos = []
                for ei in range(eh):
                    for ej in range(ew):
                        if mascara[ei][ej] == 0:
                            continue

                        ii = i + (ei - oi)
                        jj = j + (ej - oj)
                        valor = self.obter_pixel_com_fundo(matriz, ii, jj, valor_borda)
                        vizinhos.append(valor)

                saida[i][j] = callback_vizinhanca(vizinhos)

        return saida

    '''
    Executa dilatacao em tons de cinza.

    Como calcula:
    - Usa maximo da vizinhanca circular ativa.
    '''
    def dilatacao_cinza(self, matriz, _elemento_estruturante=None):
        return self._aplicar_elemento_circular_flat(matriz, 0, lambda vizinhos: max(vizinhos))

    '''
    Executa erosao em tons de cinza.

    Como calcula:
    - Usa minimo da vizinhanca circular ativa.
    '''
    def erosao_cinza(self, matriz, _elemento_estruturante=None):
        return self._aplicar_elemento_circular_flat(matriz, 255, lambda vizinhos: min(vizinhos))

    '''
    Executa abertura em tons de cinza.

    Como calcula:
    - Aplica erosao.
    - Em seguida aplica dilatacao.
    '''
    def abertura_cinza(self, matriz, _elemento_estruturante=None):
        erodida = self.erosao_cinza(matriz)
        return self.dilatacao_cinza(erodida)

    '''
    Executa fechamento em tons de cinza.

    Como calcula:
    - Aplica dilatacao.
    - Em seguida aplica erosao.
    '''
    def fechamento_cinza(self, matriz, _elemento_estruturante=None):
        dilatada = self.dilatacao_cinza(matriz)
        return self.erosao_cinza(dilatada)

    '''
    Calcula gradiente morfologico em tons de cinza.

    Como calcula:
    - Calcula dilatada e erodida.
    - Retorna subtracao: dilatada - erodida.
    '''
    def gradiente_cinza(self, matriz, _elemento_estruturante=None):
        dilatada = self.dilatacao_cinza(matriz)
        erodida = self.erosao_cinza(matriz)
        return self._subtrair_matrizes(dilatada, erodida)

    '''
    Calcula contorno externo em tons de cinza.

    Como calcula:
    - Calcula a imagem dilatada.
    - Retorna subtracao: dilatada - original.
    '''
    def contorno_externo_cinza(self, matriz, _elemento_estruturante=None):
        dilatada = self.dilatacao_cinza(matriz)
        return self._subtrair_matrizes(dilatada, matriz)

    '''
    Calcula contorno interno em tons de cinza.

    Como calcula:
    - Calcula a imagem erodida.
    - Retorna subtracao: original - erodida.
    '''
    def contorno_interno_cinza(self, matriz, _elemento_estruturante=None):
        erodida = self.erosao_cinza(matriz)
        return self._subtrair_matrizes(matriz, erodida)
