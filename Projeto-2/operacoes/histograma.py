from operacoes.base_operacoes import BaseOperacoesImagem


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

        hist_original = self.calcular_histograma(matriz)
        total_pixels = len(matriz) * len(matriz[0])

        cdf = [0] * 256
        acumulado = 0
        for i in range(256):
            acumulado += hist_original[i]
            cdf[i] = acumulado

        cdf_min = 0
        for valor in cdf:
            if valor > 0:
                cdf_min = valor
                break

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
