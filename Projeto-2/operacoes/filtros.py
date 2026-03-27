import math
from operacoes.base_operacoes import BaseOperacoesImagem, MotorConvolucao


class FiltrosImagem(BaseOperacoesImagem):

    def __init__(self):
        super().__init__()
        self.motor = MotorConvolucao(self)

    # =========================
    # FILTRO DA MÉDIA (3x3)
    # =========================
    def filtro_media(self, matriz):
        kernel = [
            [1, 1, 1],
            [1, 1, 1],
            [1, 1, 1],
        ]

        fator = 1 / 9
        
        return self.motor.aplicar(matriz, kernel, fator=fator)

    # =========================
    # FILTRO DA MEDIANA
    # =========================
    def filtro_mediana(self, matriz):
        def mediana(vizinhos, _i, _j):
            v = sorted(vizinhos)
            
            return v[len(v)//2]

        return self.motor.aplicar_janela(matriz, mediana)

    # =========================
    # PASSA-ALTA BÁSICO
    # =========================
    def filtro_passa_alta(self, matriz):
        kernel = [
            [0, -1, 0],
            [-1, 4, -1],
            [0, -1, 0],
        ]
        return self.motor.aplicar(matriz, kernel)

    # =========================
    # ROBERTS
    # =========================
    def filtro_roberts(self, matriz):
        self.validar_matriz(matriz)

        h, w = len(matriz), len(matriz[0])
        saida = self.criar_matriz(h, w, 0)

        for i in range(h):
            for j in range(w):
                z5 = self.obter_pixel_borda_replicada(matriz, i, j)
                z8 = self.obter_pixel_borda_replicada(matriz, i + 1, j)
                z6 = self.obter_pixel_borda_replicada(matriz, i, j + 1)

                gx = z5 - z8   # direção x
                gy = z5 - z6   # direção y

                saida[i][j] = self.limitar(abs(gx) + abs(gy))

        return saida

    # =========================
    # ROBERTS CRUZADO
    # =========================
    def filtro_roberts_cruzado(self, matriz):
        self.validar_matriz(matriz)

        h, w = len(matriz), len(matriz[0])
        saida = self.criar_matriz(h, w, 0)

        for i in range(h):
            for j in range(w):
                z5 = self.obter_pixel_borda_replicada(matriz, i, j)
                z9 = self.obter_pixel_borda_replicada(matriz, i + 1, j + 1)
                z6 = self.obter_pixel_borda_replicada(matriz, i, j + 1)
                z8 = self.obter_pixel_borda_replicada(matriz, i + 1, j)

                gx = z5 - z9
                gy = z6 - z8

                saida[i][j] = self.limitar(abs(gx) + abs(gy))

        return saida

    # =========================
    # PREWITT (CORRIGIDO)
    # =========================
    def filtro_prewitt(self, matriz):
        kx = [
            [-1, -1, -1],
            [0,   0,  0],
            [1,   1,  1],
        ]

        ky = [
            [-1, 0, 1],
            [-1, 0, 1],
            [-1, 0, 1],
        ]

        return self._gradiente(matriz, kx, ky)

    # =========================
    # SOBEL (CORRETO)
    # =========================
    def filtro_sobel(self, matriz):
        kx = [
            [-1, -2, -1],
            [0,   0,  0],
            [1,   2,  1],
        ]

        ky = [
            [-1, 0, 1],
            [-2, 0, 2],
            [-1, 0, 1],
        ]

        return self._gradiente(matriz, kx, ky, usar_raiz=True)

    # =========================
    # FUNÇÃO AUXILIAR GRADIENTE
    # =========================
    def _gradiente(self, matriz, kx, ky, usar_raiz=False):
        gx = self.motor.aplicar(matriz, kx, limitar_saida=False)
        gy = self.motor.aplicar(matriz, ky, limitar_saida=False)

        h, w = len(gx), len(gx[0])
        saida = self.criar_matriz(h, w, 0)

        for i in range(h):
            for j in range(w):
                if usar_raiz:
                    val = int(round(math.sqrt(gx[i][j]**2 + gy[i][j]**2)))
                else:
                    val = abs(gx[i][j]) + abs(gy[i][j])

                saida[i][j] = self.limitar(val)

        return saida

    # =========================
    # ALTO REFORÇO (HIGH BOOST)
    # =========================
    def filtro_alto_reforco(self, matriz, A=1.5):
        self.validar_matriz(matriz)

        suavizada = self.filtro_media(matriz)

        h, w = len(matriz), len(matriz[0])
        saida = self.criar_matriz(h, w, 0)

        for i in range(h):
            for j in range(w):
                mascara = matriz[i][j] - suavizada[i][j]
                valor = matriz[i][j] + A * mascara
                saida[i][j] = self.limitar(valor)

        return saida