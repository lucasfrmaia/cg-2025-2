class BaseOperacoesImagem:

    def limitar(self, valor, minimo=0, maximo=255):
        return max(minimo, min(maximo, int(round(valor))))

    def obter_pixel_com_fundo(self, matriz, x, y, fundo=0):
        if x < 0 or y < 0 or x >= len(matriz) or y >= len(matriz[0]):
            return fundo
        return matriz[x][y]

    def validar_matriz(self, matriz):
        if not matriz or not matriz[0]:
            raise ValueError("Matriz de imagem invalida.")

    def validar_dimensoes(self, matriz_a, matriz_b):
        self.validar_matriz(matriz_a)
        self.validar_matriz(matriz_b)

        if len(matriz_a) != len(matriz_b) or len(matriz_a[0]) != len(matriz_b[0]):
            raise ValueError("As imagens devem possuir as mesmas dimensoes.")

    def criar_matriz(self, altura, largura, valor=0):
        matriz = []
        for _ in range(altura):
            matriz.append([valor] * largura)
        return matriz

    def obter_pixel_borda_replicada(self, matriz, i, j):
        altura = len(matriz)
        largura = len(matriz[0])

        if i < 0:
            i = 0
        if j < 0:
            j = 0
        if i >= altura:
            i = altura - 1
        if j >= largura:
            j = largura - 1

        return matriz[i][j]

    def aplicar_por_pixel(self, matriz, callback):
        self.validar_matriz(matriz)

        altura = len(matriz)
        largura = len(matriz[0])
        saida = self.criar_matriz(altura, largura)

        for i in range(altura):
            for j in range(largura):
                saida[i][j] = self.limitar(callback(i, j))

        return saida

    def aplicar_entre_imagens(self, matriz_a, matriz_b, callback_pixel, limitar_saida=True):
        self.validar_dimensoes(matriz_a, matriz_b)

        altura = len(matriz_a)
        largura = len(matriz_a[0])
        saida = self.criar_matriz(altura, largura, 0)

        for i in range(altura):
            for j in range(largura):
                valor = callback_pixel(matriz_a[i][j], matriz_b[i][j], i, j)
                if limitar_saida:
                    valor = self.limitar(valor)
                saida[i][j] = int(round(valor))

        return saida


class MotorConvolucao:
    def __init__(self, base_operacoes=None):
        self.base = base_operacoes or BaseOperacoesImagem()

    def _get_pixel(self, matriz, i, j):
        # 🔥 GARANTE ZERO PADDING EM TUDO
        return self.base.obter_pixel_com_fundo(matriz, i, j, fundo=0)

    def aplicar(self, matriz, kernel, fator=1.0, deslocamento=0.0, callback_pos=None, limitar_saida=True):
        self.base.validar_matriz(matriz)

        h, w = len(matriz), len(matriz[0])
        kh, kw = len(kernel), len(kernel[0])

        ci, cj = kh // 2, kw // 2
        saida = self.base.criar_matriz(h, w, 0)

        for i in range(h):
            for j in range(w):
                acc = 0.0

                for ki in range(kh):
                    for kj in range(kw):
                        ii = i + (ki - ci)
                        jj = j + (kj - cj)

                        pixel = self._get_pixel(matriz, ii, jj)
                        acc += pixel * kernel[ki][kj]

                valor = acc * fator + deslocamento

                if callback_pos:
                    valor = callback_pos(valor, i, j)

                valor = int(round(valor))

                if limitar_saida:
                    valor = self.base.limitar(valor)

                saida[i][j] = valor

        return saida

    def aplicar_janela(self, matriz, callback_janela, tamanho_janela=3):
        self.base.validar_matriz(matriz)

        h, w = len(matriz), len(matriz[0])
        raio = tamanho_janela // 2

        saida = self.base.criar_matriz(h, w, 0)

        for i in range(h):
            for j in range(w):
                vizinhos = []

                for di in range(-raio, raio + 1):
                    for dj in range(-raio, raio + 1):
                        vizinhos.append(
                            self._get_pixel(matriz, i + di, j + dj)
                        )

                valor = callback_janela(vizinhos, i, j)
                saida[i][j] = self.base.limitar(int(round(valor)))

        return saida