import math
from operacoes.base_operacoes import BaseOperacoesImagem, MotorConvolucao


class FiltrosImagem(BaseOperacoesImagem):

    def __init__(self):
        super().__init__()
        self.motor = MotorConvolucao(self)

    '''
    Suaviza a imagem usando o filtro da media.

    Como calcula:
    - Aplica kernel 3x3 de uns.
    - Multiplica o acumulado por 1/9.
    '''
    def filtro_media(self, matriz):
        kernel = [
            [1, 1, 1],
            [1, 1, 1],
            [1, 1, 1],
        ]

        fator = 1 / 9
        
        return self.motor.aplicar(matriz, kernel, fator=fator)

    '''
    Remove ruido impulsivo com filtro da mediana.

    Como calcula:
    - Coleta vizinhos da janela 3x3.
    - Ordena os valores e escolhe o elemento central.
    '''
    def filtro_mediana(self, matriz):
        def mediana(vizinhos, _i, _j):
            v = sorted(vizinhos)
            
            return v[len(v)//2]

        return self.motor.aplicar_janela(matriz, mediana)

    '''
    Realca componentes de alta frequencia.

    Como calcula:
    - Convolui com kernel passa-alta laplaciano cruzado.
    '''
    def filtro_passa_alta(self, matriz):
        kernel = [
            [0, -1, 0],
            [-1, 4, -1],
            [0, -1, 0],
        ]
        return self.motor.aplicar(matriz, kernel)

    '''
    Detecta bordas com operador de Roberts.

    Como calcula:
    - Calcula gx e gy por diferencas locais.
    - Combina gradientes via |gx| + |gy|.
    '''
    def filtro_roberts(self, matriz):
        self.validar_matriz(matriz)

        h, w = len(matriz), len(matriz[0])
        saida = self.criar_matriz(h, w, 0)

        for i in range(h):
            for j in range(w):
                z5 = self.obter_pixel_borda_replicada(matriz, i, j)
                z8 = self.obter_pixel_borda_replicada(matriz, i + 1, j)
                z6 = self.obter_pixel_borda_replicada(matriz, i, j + 1)

                gx = z5 - z8
                gy = z5 - z6

                saida[i][j] = self.limitar(abs(gx) + abs(gy))

        return saida

    '''
    Detecta bordas com Roberts cruzado.

    Como calcula:
    - Usa diferencas diagonais para gx e gy.
    - Combina gradientes via |gx| + |gy|.
    '''
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

    '''
    Detecta bordas com operador de Prewitt.

    Como calcula:
    - Convolui com kx e ky.
    - Combina gradientes via |gx| + |gy|.
    '''
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

    '''
    Detecta bordas com operador de Sobel.

    Como calcula:
    - Convolui com kx e ky do Sobel.
    - Calcula magnitude por raiz de gx^2 + gy^2.
    '''
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

    '''
    Combina os gradientes horizontal e vertical.

    Como calcula:
    - Calcula gx e gy por convolucao.
    - Usa |gx| + |gy| ou sqrt(gx^2 + gy^2).
    '''
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

    '''
    Aplica filtro de alto reforco (high-boost).

    Como calcula:
    - Gera uma versao suavizada pela media.
    - Calcula mascara = original - suavizada.
    - Retorna original + A * mascara.
    '''
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