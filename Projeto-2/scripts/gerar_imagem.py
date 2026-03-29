import numpy as np

'''
Salva uma matriz numpy no formato PGM ASCII (P2).

Como calcula:
- Escreve cabecalho P2 com largura, altura e maximo 255.
- Escreve os pixels linha a linha.
'''
def salvar_pgm(nome_arquivo, matriz):
    altura, largura = matriz.shape

    with open(nome_arquivo, 'w') as f:
        f.write("P2\n")
        f.write(f"{largura} {altura}\n")
        f.write("255\n")

        for linha in matriz:
            f.write(" ".join(map(str, linha)) + "\n")


'''
Retorna uma forma base binaria para gerar imagem de exemplo.

Como calcula:
- Cria um array fixo de 0 e 1 representando a forma.
'''
def gerar_forma_base():
    return np.array([
        [0,0,0,0,1,0,0],
        [0,0,1,1,1,1,0],
        [0,0,1,1,1,1,0],
        [0,0,0,1,1,1,0],
        [0,0,1,1,1,0,0],
    ])


'''
Escala uma forma binaria usando vizinho mais proximo.

Como calcula:
- Para cada pixel de saida, projeta para coordenada de origem.
- Copia o valor da forma original.
'''
def escalar_forma(forma, nova_altura, nova_largura):
    altura, largura = forma.shape
    resultado = np.zeros((nova_altura, nova_largura), dtype=int)

    for i in range(nova_altura):
        for j in range(nova_largura):
            y = int(i * altura / nova_altura)
            x = int(j * largura / nova_largura)
            resultado[i, j] = forma[y, x]

    return resultado


'''
Gera a imagem binaria final com a forma centralizada.

Como calcula:
- Escala a forma para aproximadamente 70% da imagem.
- Calcula deslocamentos de centralizacao.
- Copia pixels ativos para valor 255.
'''
def gerar_imagem_final(largura=100, altura=100):
    img = np.zeros((altura, largura), dtype=int)

    forma = gerar_forma_base()

    nova_altura = int(altura * 0.7)
    nova_largura = int(largura * 0.7)

    forma_escalada = escalar_forma(forma, nova_altura, nova_largura)

    y_inicio = (altura - nova_altura) // 2
    x_inicio = (largura - nova_largura) // 2

    for i in range(nova_altura):
        for j in range(nova_largura):
            if forma_escalada[i, j] == 1:
                img[y_inicio + i, x_inicio + j] = 255

    return img


if __name__ == "__main__":
    imagem = gerar_imagem_final(7,   7)
    salvar_pgm("forma_centrada.pgm", imagem)
    print("Imagem gerada com sucesso!")