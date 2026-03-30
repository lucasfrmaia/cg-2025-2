from operacoes.base_operacoes import BaseOperacoesImagem
import math

class HistogramaImagem(BaseOperacoesImagem):

    def calcular_histograma(self, matriz):
        self.validar_matriz(matriz)

        histograma = [0] * 256

        for linha in matriz:
            for valor in linha:
                valor = self.limitar(valor)
                histograma[valor] += 1

        return histograma
    
    def equalizar_histograma(self, matriz):
        self.validar_matriz(matriz)

        # =========================
        # 1) HISTOGRAMA
        # =========================
        hist_original = self.calcular_histograma(matriz)

        altura = len(matriz)
        largura = len(matriz[0])
        total_pixels = altura * largura

        # =========================
        # 2) PROBABILIDADE (P(rK))
        # =========================
        prob = [0.0] * 256
        for i in range(256):
            prob[i] = hist_original[i] / total_pixels

        # =========================
        # 3) CDF NORMALIZADA (Sk)
        # =========================
        cdf = [0.0] * 256
        acumulado = 0.0

        for i in range(256):
            acumulado += prob[i]
            cdf[i] = acumulado

        # =========================
        # 4) MAPA (Round(255 * Sk))
        # =========================
        mapa = [0] * 256

        for i in range(256):
            # equivalente a (cdf * 255)
            mapeado = math.ceil(cdf[i] * 255)
            mapa[i] = self.limitar(mapeado)

        # =========================
        # 5) APLICAR NA IMAGEM
        # =========================
        matriz_equalizada = self.criar_matriz(altura, largura, 0)

        for i in range(altura):
            for j in range(largura):
                matriz_equalizada[i][j] = mapa[matriz[i][j]]

        # =========================
        # 6) HISTOGRAMA FINAL
        # =========================
        hist_equalizado = self.calcular_histograma(matriz_equalizada)

        return matriz_equalizada, hist_original, hist_equalizado, mapa

