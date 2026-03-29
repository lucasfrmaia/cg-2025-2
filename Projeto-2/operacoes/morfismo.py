from operacoes.base_operacoes import BaseOperacoesImagem


class MorfismoImagem(BaseOperacoesImagem):
    '''
    Interpola duas imagens em tons de cinza.

    Como calcula:
    - Limita t no intervalo [0, 1].
    - Para cada pixel: s = (1 - t) * A + t * B.
    '''
    def interpolar_morfismo(self, matriz_a, matriz_b, t=0.5):
        self.validar_dimensoes(matriz_a, matriz_b)

        if t < 0:
            t = 0.0
        if t > 1:
            t = 1.0

        altura = len(matriz_a)
        largura = len(matriz_a[0])
        saida = self.criar_matriz(altura, largura, 0)

        for i in range(altura):
            for j in range(largura):
                valor = (1.0 - t) * matriz_a[i][j] + t * matriz_b[i][j]
                saida[i][j] = self.limitar(int(round(valor)))

        return saida
