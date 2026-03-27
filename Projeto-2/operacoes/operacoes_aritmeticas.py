from operacoes.base_operacoes import BaseOperacoesImagem


class OperacoesAritmeticasImagem(BaseOperacoesImagem):

    def soma(self, matriz_a, matriz_b):
        return self.aplicar_entre_imagens(
            matriz_a,
            matriz_b,
            lambda a, b, _i, _j: a + b,
        )

    def subtracao(self, matriz_a, matriz_b):
        return self.aplicar_entre_imagens(
            matriz_a,
            matriz_b,
            lambda a, b, _i, _j: a - b,
        )

    def multiplicacao(self, matriz_a, matriz_b):
        return self.aplicar_entre_imagens(
            matriz_a,
            matriz_b,
            lambda a, b, _i, _j: a * b,
        )

    def divisao(self, matriz_a, matriz_b):
        return self.aplicar_entre_imagens(
            matriz_a,
            matriz_b,
            lambda a, b, _i, _j: 255 if b == 0 else a / b,
        )
