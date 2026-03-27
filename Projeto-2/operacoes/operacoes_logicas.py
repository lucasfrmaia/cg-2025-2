from operacoes.base_operacoes import BaseOperacoesImagem


class OperacoesLogicasImagem(BaseOperacoesImagem):
    def _para_binario(self, valor, limiar=127):
        return 1 if valor >= limiar else 0

    def _aplicar_binaria(self, matriz_a, matriz_b, limiar, callback_logica):
        return self.aplicar_entre_imagens(
            matriz_a,
            matriz_b,
            lambda a, b, _i, _j: 255
            if callback_logica(self._para_binario(a, limiar), self._para_binario(b, limiar)) else 0,
            limitar_saida=False,
        )

    def operacao_and(self, matriz_a, matriz_b, limiar=127):
        return self._aplicar_binaria(matriz_a, matriz_b, limiar, lambda a, b: a & b)

    def operacao_or(self, matriz_a, matriz_b, limiar=127):
        return self._aplicar_binaria(matriz_a, matriz_b, limiar, lambda a, b: a | b)

    def operacao_xor(self, matriz_a, matriz_b, limiar=127):
        return self._aplicar_binaria(matriz_a, matriz_b, limiar, lambda a, b: a ^ b)
