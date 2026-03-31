from operacoes.base_operacoes import BaseOperacoesImagem


class OperacoesLogicasImagem(BaseOperacoesImagem):
    '''
    Aplica uma operacao logica ponto a ponto entre duas imagens.

    Como calcula:
    - Usa os valores originais de A e B sem binarizacao.
    - Executa callback logico direto nos pixels.
    '''
    def _aplicar_logica(self, matriz_a, matriz_b, callback_logica):
        return self.aplicar_entre_imagens(
            matriz_a,
            matriz_b,
            lambda a, b, _i, _j: callback_logica(a, b),
            limitar_saida=False,
        )

    '''
    Executa operacao AND entre duas imagens binarias.

    Como calcula:
    - Aplica operador bit a bit a & b.
    '''
    def operacao_and(self, matriz_a, matriz_b):
        return self._aplicar_logica(matriz_a, matriz_b, lambda a, b: a & b)

    '''
    Executa operacao OR entre duas imagens binarias.

    Como calcula:
    - Aplica operador bit a bit a | b.
    '''
    def operacao_or(self, matriz_a, matriz_b):
        return self._aplicar_logica(matriz_a, matriz_b, lambda a, b: a | b)

    '''
    Executa operacao XOR entre duas imagens binarias.

    Como calcula:
    - Aplica operador bit a bit a ^ b.
    '''
    def operacao_xor(self, matriz_a, matriz_b):
        return self._aplicar_logica(matriz_a, matriz_b, lambda a, b: a ^ b)
