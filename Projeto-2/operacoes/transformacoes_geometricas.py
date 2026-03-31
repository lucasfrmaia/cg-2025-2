import math
from operacoes.base_operacoes import BaseOperacoesImagem

class TransformacoesGeometricasImagem(BaseOperacoesImagem):
    
    def escala(self, matriz, fator_x=1.0, fator_y=None, valor_fundo=0):
        self.validar_matriz(matriz)

        # Se o fator_y não for passado, assume o mesmo valor de X (escala proporcional)
        if fator_y is None:
            fator_y = fator_x

        # Evita divisão por zero ou fatores negativos que inverteriam a imagem
        if fator_x <= 0 or fator_y <= 0:
            raise ValueError("Os fatores de escala devem ser maiores que zero.")

        # PASSO 1: Descobrir o tamanho da imagem original
        altura = len(matriz)
        largura = len(matriz[0])
        
        # PASSO 2: Calcular o tamanho da nova imagem multiplicando pelos fatores.
        # Usa max(1, ...) para garantir que a imagem não suma (fique com tamanho 0) se o fator for muito pequeno.
        nova_largura = max(1, int(round(largura * fator_x)))
        nova_altura = max(1, int(round(altura * fator_y)))
        
        # Cria a tela em branco para a nova imagem
        saida = self.criar_matriz(nova_altura, nova_largura, valor_fundo)

        # PASSO 3: Mapeamento Inverso
        # Percorre cada pixel da NOVA imagem...
        for i in range(nova_altura):
            for j in range(nova_largura):
                # ... e divide pelos fatores para descobrir de qual pixel da imagem ORIGINAL ele veio.
                # O int() força o arredondamento para baixo, aplicando a interpolação por Vizinho Mais Próximo.
                origem_i = int(i / fator_y)
                origem_j = int(j / fator_x)

                # PASSO 4: Copia a cor do pixel original, desde que a coordenada calculada exista na matriz antiga.
                if 0 <= origem_i < altura and 0 <= origem_j < largura:
                    saida[i][j] = matriz[origem_i][origem_j]

        return saida

    def translacao(self, matriz, deslocamento_x=0, deslocamento_y=0, valor_fundo=0):
        self.validar_matriz(matriz)

        # Garante que o deslocamento seja um número inteiro de pixels
        deslocamento_x = int(round(deslocamento_x))
        deslocamento_y = int(round(deslocamento_y))

        altura = len(matriz)
        largura = len(matriz[0])
        
        # A imagem de saída tem o mesmo tamanho da original
        saida = self.criar_matriz(altura, largura, valor_fundo)

        # PASSO 1: Percorre a matriz de destino
        for i in range(altura):
            for j in range(largura):
                # PASSO 2: Mapeamento Inverso
                # Em Y adotamos o sentido cartesiano: deslocamento positivo move para +Y (para cima).
                origem_i = i + deslocamento_y
                origem_j = j - deslocamento_x

                # PASSO 3: Se a posição original estava dentro da imagem, copia a cor.
                # Se caiu fora (ex: origem_i negativo), o if falha e o pixel continua com a cor de fundo.
                if 0 <= origem_i < altura and 0 <= origem_j < largura:
                    saida[i][j] = matriz[origem_i][origem_j]

        return saida

    def rotacao(self, matriz, angulo_graus=0.0, valor_fundo=0):
        self.validar_matriz(matriz)

        altura = len(matriz)
        largura = len(matriz[0])
        
        # PASSO 1: Converte o ângulo para radianos
        # O sinal negativo ajusta a direção da rotação para o padrão esperado.
        angulo = math.radians(-angulo_graus)
        cos_a = math.cos(angulo)
        sin_a = math.sin(angulo)

        # PASSO 2: Encontra o centro exato da imagem original
        cx = (largura - 1) / 2.0
        cy = (altura - 1) / 2.0

        # PASSO 3: Define as coordenadas dos 4 cantos da imagem original, 
        # deslocadas para que o centro seja (0,0)
        cantos = [
            (-cx, -cy),                             # Canto superior esquerdo
            (largura - 1 - cx, -cy),                # Canto superior direito
            (-cx, altura - 1 - cy),                 # Canto inferior esquerdo
            (largura - 1 - cx, altura - 1 - cy),    # Canto inferior direito
        ]

        x_rot = []
        y_rot = []

        # PASSO 4: Gira os 4 cantos usando a matriz de rotação geométrica básica
        for x, y in cantos:
            xr = x * cos_a - y * sin_a
            yr = x * sin_a + y * cos_a
            x_rot.append(xr)
            y_rot.append(yr)

        # PASSO 5: Descobre os limites extremos dos cantos girados (Bounding Box)
        min_x = min(x_rot)
        max_x = max(x_rot)
        min_y = min(y_rot)
        max_y = max(y_rot)

        # PASSO 6: Calcula o tamanho exato da nova tela para não cortar a imagem girada
        nova_largura = int(round(max_x - min_x)) + 1
        nova_altura = int(round(max_y - min_y)) + 1
        saida = self.criar_matriz(nova_altura, nova_largura, valor_fundo)

        # Calcula o centro dessa nova tela expandida
        novo_cx = (nova_largura - 1) / 2.0
        novo_cy = (nova_altura - 1) / 2.0

        # PASSO 7: Mapeamento Inverso
        for i in range(nova_altura):
            for j in range(nova_largura):
                # Move a coordenada atual para o centro (0,0)
                xr = j - novo_cx
                yr = i - novo_cy

                # Aplica a ROTAÇÃO INVERSA (troca o sinal do seno) para achar a origem
                x_origem = xr * cos_a + yr * sin_a
                y_origem = -xr * sin_a + yr * cos_a

                # Devolve a coordenada calculada para o sistema original de matriz (0 a largura/altura)
                # O int(round(...)) atua como a interpolação por Vizinho Mais Próximo
                origem_j = int(round(x_origem + cx))
                origem_i = int(round(y_origem + cy))

                # Se o pixel calculado realmente existia na imagem antiga, copia a cor
                if 0 <= origem_i < altura and 0 <= origem_j < largura:
                    saida[i][j] = matriz[origem_i][origem_j]

        return saida

    def reflexao(self, matriz, modo="horizontal"):
        self.validar_matriz(matriz)

        altura = len(matriz)
        largura = len(matriz[0])
        saida = self.criar_matriz(altura, largura, 0)

        # PASSO 1: Espelhamento Horizontal
        if modo == "horizontal":
            for i in range(altura):
                for j in range(largura):
                    # A linha 'i' se mantém, mas pegamos a coluna 'j' de trás para frente.
                    saida[i][largura - 1 - j] = matriz[i][j]
            return saida

        # PASSO 2: Espelhamento Vertical
        if modo == "vertical":
            for i in range(altura):
                for j in range(largura):
                    # A coluna 'j' se mantém, mas pegamos a linha 'i' de trás para frente.
                    saida[altura - 1 - i][j] = matriz[i][j]
            return saida

        raise ValueError("Modo de reflexao invalido. Use horizontal ou vertical.")

    def cisalhamento(self, matriz, fator_x=0.0, fator_y=0.0, valor_fundo=0):
        self.validar_matriz(matriz)

        shear_x = fator_x
        shear_y = fator_y

        # PASSO 1: Calcula o determinante da matriz de transformação
        # Se for muito próximo de zero, a matemática colapsa e a imagem vira uma linha.
        determinante = 1.0 - (shear_x * shear_y)
        if abs(determinante) < 1e-8:
            raise ValueError("Combinacao de fatores gera transformacao nao invertivel.")

        altura = len(matriz)
        largura = len(matriz[0])

        # PASSO 2: Semelhante à rotação, pegamos os 4 cantos originais
        cantos = [
            (0, 0),
            (largura - 1, 0),
            (0, altura - 1),
            (largura - 1, altura - 1),
        ]

        x_transformado = []
        y_transformado = []
        
        # PASSO 3: Deforma os 4 cantos usando a fórmula matemática do cisalhamento
        for x, y in cantos:
            xt = x - shear_x * y
            yt = y - shear_y * x
            x_transformado.append(xt)
            y_transformado.append(yt)

        # PASSO 4: Encontra a Bounding Box (quadro limitador) da imagem distorcida
        min_x = min(x_transformado)
        max_x = max(x_transformado)
        min_y = min(y_transformado)
        max_y = max(y_transformado)

        # Calcula as novas dimensões necessárias e cria a tela de saída
        nova_largura = int(math.ceil(max_x - min_x)) + 1
        nova_altura = int(math.ceil(max_y - min_y)) + 1
        saida = self.criar_matriz(nova_altura, nova_largura, valor_fundo)

        # PASSO 5: Mapeamento Inverso
        for i in range(nova_altura):
            for j in range(nova_largura):
                # Ajusta a coordenada da tela nova em relação ao deslocamento gerado pela deformação
                xt = j + min_x
                yt = i + min_y

                # Aplica a fórmula INVERSA do cisalhamento (dividindo pelo determinante)
                # para descobrir onde esse ponto estava na imagem original retangular
                x_origem = (xt + shear_x * yt) / determinante
                y_origem = (yt + shear_y * xt) / determinante

                # Pega o pixel mais próximo (Vizinho Mais Próximo)
                origem_j = int(round(x_origem))
                origem_i = int(round(y_origem))

                # Se cair dentro da imagem original, copia a cor
                if 0 <= origem_i < altura and 0 <= origem_j < largura:
                    saida[i][j] = matriz[origem_i][origem_j]

        return saida

    def cisalhamento_arnold(self, matriz, fator_x=1.0, valor_fundo=0):
        self.validar_matriz(matriz)

        altura = len(matriz)
        largura = len(matriz[0])
        
        # PASSO 1: A Transformação do Gato de Arnold exige imagens quadradas.
        # Portanto, delimitamos a área de ação como o menor lado da imagem (n x n).
        n = min(altura, largura)
        
        # O fator_x aqui atua como o número de vezes que a imagem será "dobrada".
        iteracoes = max(1, int(fator_x))

        # Cria uma cópia da matriz original para podermos modificá-la repetidamente
        imagem_atual = [linha[:] for linha in matriz]

        # PASSO 2: Loop de iterações do Caos
        for _ in range(iteracoes):
            nova_imagem = self.criar_matriz(altura, largura, valor_fundo)

            for i in range(altura):
                for j in range(largura):
                    
                    # Se o pixel estiver DENTRO do quadrado de processamento (n x n):
                    if i < n and j < n:
                        # PASSO 3: Mapeamento Inverso do Gato de Arnold.
                        # As fórmulas matemáticas clássicas (2x + y) e (x + y) com o módulo (%)
                        # garantem que o pixel saia da borda e reapareça do outro lado da imagem.
                        origem_j = (2 * j + i) % n
                        origem_i = (j + i) % n
                        
                        # Move a cor da posição calculada para a nova matriz
                        nova_imagem[i][j] = imagem_atual[origem_i][origem_j]
                    
                    # Se o pixel estiver FORA do quadrado, não o altere
                    else:
                        nova_imagem[i][j] = imagem_atual[i][j]

            # Atualiza a imagem base para que a próxima iteração dobre a imagem que já foi dobrada
            imagem_atual = [linha[:] for linha in nova_imagem]

        return imagem_atual