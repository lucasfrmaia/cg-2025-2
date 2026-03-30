from operacoes.base_operacoes import BaseOperacoesImagem


class MorfismoImagem(BaseOperacoesImagem):
    '''
    Interpola duas imagens em tons de cinza.

    Como calcula:
    - Limita t no intervalo [0, 1].
    - Para cada pixel: s = (1 - t) * A + t * B.
    '''
    def interpolar_morfismo(self, matriz_a, matriz_b, t=0.5):
        self.validar_dimensoes(matriz_a, matriz_b)

        if t < 0:
            t = 0.0
        if t > 1:
            t = 1.0

        def morfismo_formula(pixelA, pixelB, i, j):
            return (1.0 - t) * pixelA + t * pixelB  

        return self.aplicar_entre_imagens(
            matriz_a,
            matriz_b,
            morfismo_formula,
            limitar_saida=True
        )

      
