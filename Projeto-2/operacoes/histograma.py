from operacoes.base_operacoes import BaseOperacoesImagem
import math

class HistogramaImagem(BaseOperacoesImagem):
    '''
    Calcula o histograma da imagem em 256 niveis.

    Como calcula:
    - Inicializa vetor de 256 posicoes com zero.
    - Incrementa a frequencia correspondente de cada pixel.
    '''
    def calcular_histograma(self, matriz):
        self.validar_matriz(matriz)
        histograma = [0] * 256

        for linha in matriz:
            for valor in linha:
                valor = self.limitar(valor)
                histograma[valor] += 1

        return histograma

    '''
    Equaliza o histograma para melhorar contraste.

    Como calcula:
    - Calcula o histograma e a CDF acumulada.
    - Gera mapa de tons para 0..255.
    - Aplica o mapa em todos os pixels.
    '''
    def equalizar_histograma(self, matriz):
        self.validar_matriz(matriz)

        hist_original = self.calcular_histograma(matriz)
        total_pixels = len(matriz) * len(matriz[0])

        cdf = [0] * 256
        acumulado = 0
        for i in range(256):
            acumulado += hist_original[i]
            cdf[i] = acumulado

        cdf_min = next(x for x in cdf if x > 0)

        mapa = [0] * 256
        denominador = total_pixels - cdf_min

        for i in range(256):
            if denominador <= 0:
                mapeado = i
            else:
                mapeado = round(((cdf[i] - cdf_min) / denominador) * 255)

            mapa[i] = self.limitar(mapeado)

        altura = len(matriz)
        largura = len(matriz[0])
        matriz_equalizada = self.criar_matriz(altura, largura, 0)

        for i in range(altura):
            for j in range(largura):
                matriz_equalizada[i][j] = mapa[matriz[i][j]]

        hist_equalizado = self.calcular_histograma(matriz_equalizada)
        
        return matriz_equalizada, hist_original, hist_equalizado, mapa
