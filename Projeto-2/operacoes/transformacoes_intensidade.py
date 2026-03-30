import math
from operacoes.base_operacoes import BaseOperacoesImagem


class TransformacoesIntensidadeImagem(BaseOperacoesImagem):
    '''
    Gera o negativo da imagem.

    Como calcula:
    - Para cada pixel r, retorna s = 255 - r.
    '''
    def negativo(self, matriz):
        self.validar_matriz(matriz)

        def formula(i, j):
            return 255 - matriz[i][j]

        return self.aplicar_por_pixel(matriz, formula)

    '''
    Aplica transformacao gamma.

    Como calcula:
    - Normaliza r para [0, 1].
    - Calcula s = c * (r ** gamma).
    - Reescala para [0, 255].
    '''
    def transformacao_gamma(self, matriz, c=1.0, gamma=1.0):
        self.validar_matriz(matriz)

        def formula(i, j):
            r = matriz[i][j] / 255.0
            s = c * (r ** gamma)
            return s * 255.0

        return self.aplicar_por_pixel(matriz, formula)

    '''
    Aplica transformacao logaritmica nos tons de cinza.

    Como calcula:
    - Para cada pixel r, calcula s = a * log(r + 1).
    - Limita o resultado para o intervalo valido.
    '''
    def transformacao_logaritmica(self, matriz, a=45.0):
        self.validar_matriz(matriz)

        def formula(i, j):
            return a * math.log(matriz[i][j] + 1, 10)

        return self.aplicar_por_pixel(matriz, formula)

    '''
    Aplica funcao de transferencia geral centrada em w.

    Como calcula:
    - Para cada pixel r, usa a funcao sigmoide deslocada.
    - Formula: s(r) = 255 - 1 / (1 + e^(-((r - w) / sigma))).
    '''
    def funcao_transferencia_geral(self, matriz, w=128.0, sigma=20.0):
        self.validar_matriz(matriz)
        if sigma == 0:
            raise ValueError("sigma deve ser diferente de zero.")

        def formula(i, j):
            r = matriz[i][j]
            expoente = -((r - w) / sigma)
            
            if expoente > 60:
                expoente = 60
            if expoente < -60:
                expoente = -60

            # Esse math.ex faz e ^ expoente
            denominador = 1.0 + math.exp(expoente)
            
            return 255.0 / denominador

        return self.aplicar_por_pixel(matriz, formula)

    '''
    Ajusta a faixa dinamica para um valor alvo.

    Como calcula:
    - Calcula f_min e f_max da imagem.
    - Reescala cada pixel para o intervalo [0, target].
    '''
    def faixa_dinamica(self, matriz, target=255.0):
        self.validar_matriz(matriz)

        altura = len(matriz)
        largura = len(matriz[0])

        # Determina os extremos reais da imagem para normalizacao.
        f_min = float('inf')
        f_max = float('-inf')

        for i in range(altura):
            for j in range(largura):
                valor = matriz[i][j]

                if valor < f_min:
                    f_min = valor
                if valor > f_max:
                    f_max = valor

        def formula(i, j):
            r = matriz[i][j]

            # Caso degenerado (imagem constante)
            if f_max == f_min:
                return 0

            s = ((r - f_min) / (f_max - f_min)) * target

            return s

        return self.aplicar_por_pixel(matriz, formula)

    '''
    Aplica transformacao linear de intensidade.

    Como calcula:
    - Para cada pixel r, calcula s = alpha * r + beta.
    - Limita o resultado para 0..255.
    '''
    def linear(self, matriz, alpha=1.0, beta=0.0):
        self.validar_matriz(matriz)

        def formula(i, j):
            return alpha * matriz[i][j] + beta

        return self.aplicar_por_pixel(matriz, formula)
