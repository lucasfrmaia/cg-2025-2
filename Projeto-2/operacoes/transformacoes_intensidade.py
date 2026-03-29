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

        altura = len(matriz)
        largura = len(matriz[0])
        saida = self.criar_matriz(altura, largura, 0)

        for i in range(altura):
            for j in range(largura):
                saida[i][j] = 255 - matriz[i][j]

        return saida

    '''
    Aplica transformacao gamma.

    Como calcula:
    - Normaliza r para [0, 1].
    - Calcula s = c * (r ** gamma).
    - Reescala para [0, 255].
    '''
    def transformacao_gamma(self, matriz, c=1.0, gamma=1.0):
        self.validar_matriz(matriz)

        altura = len(matriz)
        largura = len(matriz[0])
        saida = self.criar_matriz(altura, largura, 0)

        for i in range(altura):
            for j in range(largura):
                r = matriz[i][j] / 255.0
                s = c * (r ** gamma)
                saida[i][j] = self.limitar(int(round(s * 255.0)))

        return saida

    '''
    Aplica transformacao logaritmica nos tons de cinza.

    Como calcula:
    - Para cada pixel r, calcula s = a * log(r + 1).
    - Limita o resultado para o intervalo valido.
    '''
    def transformacao_logaritmica(self, matriz, a=45.0):
        self.validar_matriz(matriz)

        altura = len(matriz)
        largura = len(matriz[0])
        saida = self.criar_matriz(altura, largura, 0)

        for i in range(altura):
            for j in range(largura):
                s = a * math.log(matriz[i][j] + 1)
                saida[i][j] = self.limitar(int(round(s)))

        return saida

    '''
    Aplica funcao janela linear centrada em w.

    Como calcula:
    - Define minimo = w - largura/2 e maximo = w + largura/2.
    - Mapeia abaixo do minimo para 0 e acima do maximo para 255.
    - Interpola linearmente dentro da janela.
    '''
    def funcao_janela(self, matriz, w=128.0, largura=80.0):
        self.validar_matriz(matriz)
        if largura <= 0:
            raise ValueError("A largura da janela deve ser maior que zero.")

        minimo = w - (largura / 2.0)
        maximo = w + (largura / 2.0)

        altura = len(matriz)
        largura_m = len(matriz[0])
        saida = self.criar_matriz(altura, largura_m, 0)

        for i in range(altura):
            for j in range(largura_m):
                r = matriz[i][j]
                if r <= minimo:
                    s = 0
                elif r >= maximo:
                    s = 255
                else:
                    s = ((r - minimo) / largura) * 255.0

                saida[i][j] = self.limitar(int(round(s)))

        return saida

    '''
    Ajusta a faixa dinamica entre r_min e r_max.

    Como calcula:
    - Mapeia r <= r_min para 0 e r >= r_max para 255.
    - Interpola linearmente valores dentro do intervalo.
    '''
    def faixa_dinamica(self, matriz, r_min=0.0, r_max=255.0):
        self.validar_matriz(matriz)
        if r_max <= r_min:
            raise ValueError("r_max deve ser maior que r_min.")

        altura = len(matriz)
        largura = len(matriz[0])
        saida = self.criar_matriz(altura, largura, 0)

        for i in range(altura):
            for j in range(largura):
                r = matriz[i][j]
                if r <= r_min:
                    s = 0
                elif r >= r_max:
                    s = 255
                else:
                    s = ((r - r_min) / (r_max - r_min)) * 255.0

                saida[i][j] = self.limitar(int(round(s)))

        return saida

    '''
    Aplica transformacao linear de intensidade.

    Como calcula:
    - Para cada pixel r, calcula s = alpha * r + beta.
    - Limita o resultado para 0..255.
    '''
    def linear(self, matriz, alpha=1.0, beta=0.0):
        self.validar_matriz(matriz)

        altura = len(matriz)
        largura = len(matriz[0])
        saida = self.criar_matriz(altura, largura, 0)

        for i in range(altura):
            for j in range(largura):
                s = alpha * matriz[i][j] + beta
                saida[i][j] = self.limitar(int(round(s)))

        return saida
