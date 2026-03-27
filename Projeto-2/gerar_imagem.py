import numpy as np

def salvar_pgm(nome_arquivo, matriz):
    altura, largura = matriz.shape

    with open(nome_arquivo, 'w') as f:
        f.write("P2\n")
        f.write(f"{largura} {altura}\n")
        f.write("255\n")

        for linha in matriz:
            f.write(" ".join(map(str, linha)) + "\n")


def gerar_forma_base():
    """
    Forma baseada na imagem que você enviou
    """
    return np.array([
        [0,0,0,0,1,0,0],
        [0,0,1,1,1,1,0],
        [0,0,1,1,1,1,0],
        [0,0,0,1,1,1,0],
        [0,0,1,1,1,0,0],
    ])


def escalar_forma(forma, nova_altura, nova_largura):
    """
    Escala a forma usando nearest neighbor (simples e ideal pra binário)
    """
    altura, largura = forma.shape
    resultado = np.zeros((nova_altura, nova_largura), dtype=int)

    for i in range(nova_altura):
        for j in range(nova_largura):
            y = int(i * altura / nova_altura)
            x = int(j * largura / nova_largura)
            resultado[i, j] = forma[y, x]

    return resultado


def gerar_imagem_final(largura=100, altura=100):
    img = np.zeros((altura, largura), dtype=int)

    forma = gerar_forma_base()

    # Escala para ~70% da imagem
    nova_altura = int(altura * 0.7)
    nova_largura = int(largura * 0.7)

    forma_escalada = escalar_forma(forma, nova_altura, nova_largura)

    # Centralizar
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