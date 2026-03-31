import os

ALTURA = 256
LARGURA = 256

PASTA_SAIDA = "saida_morfologia"
os.makedirs(PASTA_SAIDA, exist_ok=True)


def criar_matriz(h, w, valor=0):
    return [[valor for _ in range(w)] for _ in range(h)]


def salvar_pgm(caminho, matriz):
    with open(caminho, "w") as f:
        f.write("P2\n")
        f.write(f"{LARGURA} {ALTURA}\n")
        f.write("255\n")

        for linha in matriz:
            f.write(" ".join(str(v) for v in linha) + "\n")


def gerar_imagem_teste():
    img = criar_matriz(ALTURA, LARGURA, 0)

    # =========================
    # 1. QUADRADO GRANDE (estrutura principal)
    # =========================
    for i in range(60, 196):
        for j in range(60, 196):
            img[i][j] = 255

    # =========================
    # 2. BURACO INTERNO (para fechamento)
    # =========================
    for i in range(100, 156):
        for j in range(100, 156):
            img[i][j] = 0

    # =========================
    # 3. QUADRADO PEQUENO (para erosão sumir)
    # =========================
    for i in range(120, 136):
        for j in range(120, 136):
            img[i][j] = 255

    # =========================
    # 4. LINHA FINA (para abertura remover)
    # =========================
    for j in range(30, 226):
        img[30][j] = 255

    # =========================
    # 5. RUÍDO ESPALHADO (abertura limpa)
    # =========================
    for i in range(40, 200, 5):
        for j in range(40, 200, 7):
            img[i][j] = 255

    # =========================
    # 6. BORDA INTERNA (para gradiente destacar)
    # =========================
    for i in range(80, 176):
        img[i][80] = 0
        img[i][176] = 0
    for j in range(80, 176):
        img[80][j] = 0
        img[176][j] = 0

    return img


if __name__ == "__main__":
    imagem = gerar_imagem_teste()
    salvar_pgm(os.path.join(PASTA_SAIDA, "imagem_base.pgm"), imagem)

    print("Imagem gerada com sucesso em:", PASTA_SAIDA)