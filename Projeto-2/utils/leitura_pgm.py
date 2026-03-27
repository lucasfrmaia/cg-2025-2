def ler_pgm(caminho_arquivo):
    """Le um arquivo PGM (P2/ASCII) e retorna a matriz em escala 0..255."""
    tokens = []

    with open(caminho_arquivo, "r", encoding="utf-8") as arquivo:
        for linha in arquivo:
            linha = linha.strip()
            if not linha:
                continue

            if linha.startswith("#"):
                continue

            if "#" in linha:
                linha = linha.split("#", 1)[0].strip()

            if linha:
                tokens.extend(linha.split())

    if len(tokens) < 4:
        raise ValueError("Arquivo PGM invalido: cabecalho incompleto.")

    if tokens[0] != "P2":
        raise ValueError("Formato invalido. Apenas PGM P2 (ASCII) e suportado.")

    try:
        largura = int(tokens[1])
        altura = int(tokens[2])
        maximo_original = int(tokens[3])
    except ValueError as erro:
        raise ValueError("Cabecalho PGM contem valores invalidos.") from erro

    if largura <= 0 or altura <= 0:
        raise ValueError("Dimensoes invalidas no cabecalho PGM.")

    if maximo_original <= 0:
        raise ValueError("Valor maximo invalido no cabecalho PGM.")

    quantidade_pixels = largura * altura
    dados = tokens[4:]

    if len(dados) < quantidade_pixels:
        raise ValueError("Arquivo PGM invalido: quantidade insuficiente de pixels.")

    matriz = []
    indice = 0
    for _ in range(altura):
        linha_pixels = []
        for _ in range(largura):
            valor_lido = int(dados[indice])
            indice += 1

            if valor_lido < 0:
                valor_lido = 0
            if valor_lido > maximo_original:
                valor_lido = maximo_original

            # Normaliza tudo para 8 bits, simplificando as operacoes seguintes.
            valor_8bits = round((valor_lido / maximo_original) * 255)
            linha_pixels.append(valor_8bits)

        matriz.append(linha_pixels)

    return matriz, 255
