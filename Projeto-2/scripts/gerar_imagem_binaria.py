import numpy as np
import os

# =========================
# CONFIG
# =========================
OUTPUT_DIR = "saida_morfologia_p2"
SIZE = 256

PIXEL_ATIVO = 255
PIXEL_FUNDO = 0

os.makedirs(OUTPUT_DIR, exist_ok=True)

# =========================
# SALVAR P2 (ASCII)
# =========================
def salvar_p2(imagem, caminho):
    h, w = imagem.shape

    with open(caminho, "w") as f:
        f.write("P2\n")
        f.write(f"{w} {h}\n")
        f.write("255\n")

        for i in range(h):
            for j in range(w):
                f.write(f"{int(imagem[i][j])} ")
            f.write("\n")


# =========================
# GERAR IMAGEM IDEAL
# =========================
def gerar_imagem_teste():
    img = np.zeros((SIZE, SIZE), dtype=np.uint8)

    # =========================
    # 1. QUADRADO PRINCIPAL
    # =========================
    s = int(SIZE * 0.5)
    start = (SIZE - s) // 2
    img[start:start+s, start:start+s] = PIXEL_ATIVO

    # =========================
    # 2. BURACO INTERNO (fechamento)
    # =========================
    hole = int(s * 0.3)
    hs = start + (s - hole) // 2
    img[hs:hs+hole, hs:hs+hole] = PIXEL_FUNDO

    # =========================
    # 3. LINHAS FINAS (erosão remove)
    # =========================
    for i in range(30, 220):
        img[i, 40] = PIXEL_ATIVO
        img[40, i] = PIXEL_ATIVO

    # =========================
    # 4. RUÍDO (abertura remove)
    # =========================
    np.random.seed(42)
    ruido = np.random.rand(SIZE, SIZE) > 0.97
    img[ruido] = PIXEL_ATIVO

    # =========================
    # 5. BORDA FINA EXTRA
    # =========================
    img[start:start+s, start] = PIXEL_ATIVO
    img[start:start+s, start+s-1] = PIXEL_ATIVO

    return img


# =========================
# MORFOLOGIA BINÁRIA
# =========================
def pad(img):
    return np.pad(img, 1, mode='constant', constant_values=PIXEL_FUNDO)

def dilatacao(img):
    h, w = img.shape
    padded = pad(img)
    out = np.zeros_like(img)

    for i in range(h):
        for j in range(w):
            if np.any(padded[i:i+3, j:j+3] == PIXEL_ATIVO):
                out[i][j] = PIXEL_ATIVO
    return out


def erosao(img):
    h, w = img.shape
    padded = pad(img)
    out = np.zeros_like(img)

    for i in range(h):
        for j in range(w):
            if np.all(padded[i:i+3, j:j+3] == PIXEL_ATIVO):
                out[i][j] = PIXEL_ATIVO
    return out


def abertura(img):
    return dilatacao(erosao(img))


def fechamento(img):
    return erosao(dilatacao(img))


def gradiente(img):
    return dilatacao(img) - erosao(img)


def contorno_externo(img):
    return dilatacao(img) - img


def contorno_interno(img):
    return img - erosao(img)


# =========================
# EXECUÇÃO
# =========================
img = gerar_imagem_teste()

resultados = {
    "original": img,
    "dilatacao": dilatacao(img),
    "erosao": erosao(img),
    "abertura": abertura(img),
    "fechamento": fechamento(img),
    "gradiente": gradiente(img),
    "contorno_externo": contorno_externo(img),
    "contorno_interno": contorno_interno(img),
}

# =========================
# SALVAR
# =========================
for nome, imagem in resultados.items():
    caminho = os.path.join(OUTPUT_DIR, f"{nome}.pgm")
    salvar_p2(imagem, caminho)

print("Imagens geradas com fundo preto (0) e objeto branco (255) em:", OUTPUT_DIR)