from PIL import Image

# =========================
# CONFIGURAÇÃO (paths fixos)
# =========================
CAMINHO_ENTRADA = "scripts/Screenshot_2.png"
CAMINHO_SAIDA = "scripts/Screenshot_2.ogm"

# Tamanho padrão (todas as imagens terão esse tamanho)
LARGURA_PADRAO = 101
ALTURA_PADRAO = 101

def salvar_pgm_ascii(imagem_path, output_path):
    # Abre e converte para grayscale
    img = Image.open(imagem_path).convert("L")

    # Redimensiona a imagem
    img = img.resize((LARGURA_PADRAO, ALTURA_PADRAO), Image.BILINEAR)

    largura, altura = img.size
    pixels = list(img.getdata())

    with open(output_path, "w") as f:
        f.write("P2\n")
        f.write(f"{largura} {altura}\n")
        f.write("255\n")

        for i in range(altura):
            linha = pixels[i * largura:(i + 1) * largura]
            f.write(" ".join(map(str, linha)) + "\n")

    print(f"Imagem salva com tamanho {largura}x{altura}")

# Executa
salvar_pgm_ascii(CAMINHO_ENTRADA, CAMINHO_SAIDA)