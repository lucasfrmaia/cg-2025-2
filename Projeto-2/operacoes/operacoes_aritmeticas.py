from operacoes.base_operacoes import BaseOperacoesImagem


class OperacoesAritmeticasImagem(BaseOperacoesImagem):

    '''
    Soma duas imagens pixel a pixel.

    Como calcula:
    - Para cada posicao, calcula a + b.
    - Aplica limitacao para manter o intervalo valido.
    '''
    def soma(self, matriz_a, matriz_b):
        return self.aplicar_entre_imagens(
            matriz_a,
            matriz_b,
            lambda a, b, _i, _j: a + b,
        )

    '''
    Subtrai uma imagem da outra pixel a pixel.

    Como calcula:
    - Para cada posicao, calcula a - b.
    - Aplica limitacao para manter o intervalo valido.
    '''
    def subtracao(self, matriz_a, matriz_b):
        return self.aplicar_entre_imagens(
            matriz_a,
            matriz_b,
            lambda a, b, _i, _j: a - b,
        )

    '''
    Multiplica duas imagens pixel a pixel.

    Como calcula:
    - Para cada posicao, calcula a * b.
    - Aplica limitacao para manter o intervalo valido.
    '''
    def multiplicacao(self, matriz_a, matriz_b):
        return self.aplicar_entre_imagens(
            matriz_a,
            matriz_b,
            lambda a, b, _i, _j: a * b,
        )

    '''
    Divide duas imagens pixel a pixel.

    Como calcula:
    - Quando b == 0, retorna 255 para evitar divisao por zero.
    - Caso contrario, calcula a / b.
    '''
    def divisao(self, matriz_a, matriz_b):
        return self.aplicar_entre_imagens(
            matriz_a,
            matriz_b,
            lambda a, b, _i, _j: 255 if b == 0 else a / b,
        )
