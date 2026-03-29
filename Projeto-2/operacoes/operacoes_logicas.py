from operacoes.base_operacoes import BaseOperacoesImagem


class OperacoesLogicasImagem(BaseOperacoesImagem):
    LIMIAR_BINARIO = 127

    '''
    Converte um pixel para representacao binaria.

    Como calcula:
    - Retorna 1 se valor >= limiar.
    - Retorna 0 caso contrario.
    '''
    def _para_binario(self, valor):
        return 1 if valor >= self.LIMIAR_BINARIO else 0

    '''
    Aplica uma operacao logica binaria entre duas imagens.

    Como calcula:
    - Binariza os pixels de A e B.
    - Executa callback logico.
    - Retorna 255 para verdadeiro e 0 para falso.
    '''
    def _aplicar_binaria(self, matriz_a, matriz_b, callback_logica):
        return self.aplicar_entre_imagens(
            matriz_a,
            matriz_b,
            lambda a, b, _i, _j: 255
            if callback_logica(self._para_binario(a), self._para_binario(b)) else 0,
            limitar_saida=False,
        )

    '''
    Executa operacao AND entre duas imagens binarias.

    Como calcula:
    - Converte ambos os pixels para 0/1.
    - Aplica operador a & b.
    '''
    def operacao_and(self, matriz_a, matriz_b):
        return self._aplicar_binaria(matriz_a, matriz_b, lambda a, b: a & b)

    '''
    Executa operacao OR entre duas imagens binarias.

    Como calcula:
    - Converte ambos os pixels para 0/1.
    - Aplica operador a | b.
    '''
    def operacao_or(self, matriz_a, matriz_b):
        return self._aplicar_binaria(matriz_a, matriz_b, lambda a, b: a | b)

    '''
    Executa operacao XOR entre duas imagens binarias.

    Como calcula:
    - Converte ambos os pixels para 0/1.
    - Aplica operador a ^ b.
    '''
    def operacao_xor(self, matriz_a, matriz_b):
        return self._aplicar_binaria(matriz_a, matriz_b, lambda a, b: a ^ b)
