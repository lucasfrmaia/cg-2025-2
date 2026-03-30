from operacoes.base_operacoes import BaseOperacoesImagem


class OperacoesAritmeticasImagem(BaseOperacoesImagem):

    def _finalizar_resultado(self, matriz, normalizar=False):
        if normalizar:
            return self.normalizar_matriz(matriz)

        return [
            [self.limitar(valor) for valor in linha]
            for linha in matriz
        ]

    '''
    Soma duas imagens pixel a pixel.

    Como calcula:
    - Para cada posicao, calcula a + b.
    - Aplica limitacao para manter o intervalo valido.
    '''
    def soma(self, matriz_a, matriz_b, normalizar=False):
        resultado = self.aplicar_entre_imagens(
            matriz_a,
            matriz_b,
            lambda a, b, _i, _j: a + b,
            limitar_saida=False,
        )
        return self._finalizar_resultado(resultado, normalizar)

    '''
    Subtrai uma imagem da outra pixel a pixel.

    Como calcula:
    - Para cada posicao, calcula a - b.
    - Aplica limitacao para manter o intervalo valido.
    '''
    def subtracao(self, matriz_a, matriz_b, normalizar=False):
        resultado = self.aplicar_entre_imagens(
            matriz_a,
            matriz_b,
            lambda a, b, _i, _j: a - b,
            limitar_saida=False,
        )
        return self._finalizar_resultado(resultado, normalizar)

    '''
    Multiplica duas imagens pixel a pixel.

    Como calcula:
    - Para cada posicao, calcula a * b.
    - Aplica limitacao para manter o intervalo valido.
    '''
    def multiplicacao(self, matriz_a, matriz_b, normalizar=False):
        resultado = self.aplicar_entre_imagens(
            matriz_a,
            matriz_b,
            lambda a, b, _i, _j: a * b,
            limitar_saida=False,
        )
        return self._finalizar_resultado(resultado, normalizar)

    '''
    Divide duas imagens pixel a pixel.

    Como calcula:
    - Quando b == 0, retorna 255 para evitar divisao por zero.
    - Caso contrario, calcula a / b.
    '''
    def divisao(self, matriz_a, matriz_b, normalizar=False):
        resultado = self.aplicar_entre_imagens(
            matriz_a,
            matriz_b,
            lambda a, b, _i, _j: 255 if b == 0 else a / b,
            limitar_saida=False,
        )
        return self._finalizar_resultado(resultado, normalizar)
