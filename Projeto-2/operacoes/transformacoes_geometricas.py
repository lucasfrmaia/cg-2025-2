import math
from operacoes.base_operacoes import BaseOperacoesImagem


class TransformacoesGeometricasImagem(BaseOperacoesImagem):
    def escala(self, matriz, fator_x=1.0, fator_y=None):
        self.validar_matriz(matriz)

        if fator_y is None:
            fator_y = fator_x

        if fator_x <= 0 or fator_y <= 0:
            raise ValueError("Os fatores de escala devem ser maiores que zero.")

        altura = len(matriz)
        largura = len(matriz[0])
        nova_largura = max(1, int(round(largura * fator_x)))
        nova_altura = max(1, int(round(altura * fator_y)))
        saida = self.criar_matriz(nova_altura, nova_largura, 0)

        for i in range(nova_altura):
            for j in range(nova_largura):
                origem_i = int(i / fator_y)
                origem_j = int(j / fator_x)

                if origem_i >= altura:
                    origem_i = altura - 1
                if origem_j >= largura:
                    origem_j = largura - 1

                saida[i][j] = matriz[origem_i][origem_j]

        return saida

    def translacao(self, matriz, deslocamento_x=0, deslocamento_y=0, valor_fundo=0):
        self.validar_matriz(matriz)

        altura = len(matriz)
        largura = len(matriz[0])
        saida = self.criar_matriz(altura, largura, valor_fundo)

        for i in range(altura):
            for j in range(largura):
                novo_i = i + deslocamento_y
                novo_j = j + deslocamento_x
                if 0 <= novo_i < altura and 0 <= novo_j < largura:
                    saida[novo_i][novo_j] = matriz[i][j]

        return saida

    def rotacao(self, matriz, angulo_graus=0.0, valor_fundo=0):
        self.validar_matriz(matriz)

        altura = len(matriz)
        largura = len(matriz[0])
        angulo = math.radians(-angulo_graus)
        cos_a = math.cos(angulo)
        sin_a = math.sin(angulo)

        cx = (largura - 1) / 2.0
        cy = (altura - 1) / 2.0

        cantos = [
            (-cx, -cy),
            (largura - 1 - cx, -cy),
            (-cx, altura - 1 - cy),
            (largura - 1 - cx, altura - 1 - cy),
        ]

        x_rot = []
        y_rot = []
        for x, y in cantos:
            xr = x * cos_a - y * sin_a
            yr = x * sin_a + y * cos_a
            x_rot.append(xr)
            y_rot.append(yr)

        min_x = min(x_rot)
        max_x = max(x_rot)
        min_y = min(y_rot)
        max_y = max(y_rot)

        nova_largura = int(round(max_x - min_x)) + 1
        nova_altura = int(round(max_y - min_y)) + 1
        saida = self.criar_matriz(nova_altura, nova_largura, valor_fundo)

        novo_cx = (nova_largura - 1) / 2.0
        novo_cy = (nova_altura - 1) / 2.0

        for i in range(nova_altura):
            for j in range(nova_largura):
                xr = j - novo_cx
                yr = i - novo_cy

                x_origem = xr * cos_a + yr * sin_a
                y_origem = -xr * sin_a + yr * cos_a

                origem_j = int(round(x_origem + cx))
                origem_i = int(round(y_origem + cy))

                if 0 <= origem_i < altura and 0 <= origem_j < largura:
                    saida[i][j] = matriz[origem_i][origem_j]

        return saida

    def reflexao(self, matriz, modo="horizontal"):
        self.validar_matriz(matriz)

        altura = len(matriz)
        largura = len(matriz[0])
        saida = self.criar_matriz(altura, largura, 0)

        if modo == "horizontal":
            for i in range(altura):
                for j in range(largura):
                    saida[i][largura - 1 - j] = matriz[i][j]
            return saida

        if modo == "vertical":
            for i in range(altura):
                for j in range(largura):
                    saida[altura - 1 - i][j] = matriz[i][j]
            return saida

        if modo == "diagonal":
            nova = self.criar_matriz(largura, altura, 0)
            for i in range(altura):
                for j in range(largura):
                    nova[j][i] = matriz[i][j]
            return nova

        raise ValueError("Modo de reflexao invalido. Use horizontal, vertical ou diagonal.")

    def cisalhamento(self, matriz, fator_x=0.0, fator_y=0.0, valor_fundo=0):
        self.validar_matriz(matriz)

        determinante = 1.0 - (fator_x * fator_y)
        if abs(determinante) < 1e-8:
            raise ValueError("Combinacao de fatores gera transformacao nao invertivel.")

        altura = len(matriz)
        largura = len(matriz[0])

        cantos = [
            (0, 0),
            (largura - 1, 0),
            (0, altura - 1),
            (largura - 1, altura - 1),
        ]

        x_transformado = []
        y_transformado = []
        for x, y in cantos:
            xt = x + fator_x * y
            yt = y + fator_y * x
            x_transformado.append(xt)
            y_transformado.append(yt)

        min_x = min(x_transformado)
        max_x = max(x_transformado)
        min_y = min(y_transformado)
        max_y = max(y_transformado)

        nova_largura = int(math.ceil(max_x - min_x)) + 1
        nova_altura = int(math.ceil(max_y - min_y)) + 1
        saida = self.criar_matriz(nova_altura, nova_largura, valor_fundo)

        for i in range(nova_altura):
            for j in range(nova_largura):
                xt = j + min_x
                yt = i + min_y

                x_origem = (xt - fator_x * yt) / determinante
                y_origem = (yt - fator_y * xt) / determinante

                origem_j = int(round(x_origem))
                origem_i = int(round(y_origem))

                if 0 <= origem_i < altura and 0 <= origem_j < largura:
                    saida[i][j] = matriz[origem_i][origem_j]

        return saida
