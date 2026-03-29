# Documentacao Tecnica do Projeto

## Escopo desta documentacao

Esta documentacao cobre as funcoes dos modulos de processamento e utilitarios do projeto.
A camada de front-end em `ui` (incluindo a classe da interface) foi intencionalmente excluida, conforme solicitado.

## Estrutura geral

- Entrada da aplicacao: `main.py`
- Processamento de imagem: `operacoes/`
- Leitura de arquivo PGM: `utils/leitura_pgm.py`
- Script auxiliar de geracao de imagem: `scripts/gerar_imagem.py`

Arquivos `__init__.py`:

- `operacoes/__init__.py`: arquivo de pacote sem funcoes.
- `utils/__init__.py`: arquivo de pacote sem funcoes.

---

## main.py

### iniciar aplicacao (fluxo principal)

`main.py` importa `iniciar_aplicacao` do modulo de interface e inicia a aplicacao.

Como funciona:

1. Ao executar o arquivo, o bloco `if __name__ == "__main__":` eh acionado.
2. A funcao `iniciar_aplicacao()` eh chamada para abrir a interface grafica.

---

## operacoes/base_operacoes.py

## Classe `BaseOperacoesImagem`

### `limitar(valor, minimo=0, maximo=255)`

O que faz:

- Clampa qualquer valor para o intervalo permitido de intensidade.

Como calcula:

- Faz `round(valor)`.
- Aplica `max(minimo, min(maximo, valor_arredondado))`.

### `obter_pixel_com_fundo(matriz, x, y, fundo=0)`

O que faz:

- Retorna um pixel da matriz com tratamento de borda por valor constante.

Como calcula:

- Se `(x, y)` estiver fora da imagem, retorna `fundo`.
- Caso contrario, retorna `matriz[x][y]`.

### `validar_matriz(matriz)`

O que faz:

- Garante que a matriz nao seja vazia.

Como calcula:

- Verifica se `matriz` existe e se `matriz[0]` existe.
- Lanca excecao quando invalida.

### `validar_dimensoes(matriz_a, matriz_b)`

O que faz:

- Verifica se duas imagens podem ser processadas juntas.

Como calcula:

- Valida as duas matrizes.
- Compara altura e largura de ambas.

### `criar_matriz(altura, largura, valor=0)`

O que faz:

- Cria uma matriz preenchida com valor inicial.

Como calcula:

- Cria lista de listas com `altura` linhas e `largura` colunas.

### `obter_pixel_borda_replicada(matriz, i, j)`

O que faz:

- Acessa pixel com estrategia de borda replicada.

Como calcula:

- Ajusta `i` e `j` para o intervalo valido mais proximo.
- Retorna o pixel da posicao ajustada.

### `aplicar_por_pixel(matriz, callback)`

O que faz:

- Aplica transformacao pontual pixel a pixel.

Como calcula:

- Para cada `(i, j)`, calcula `callback(i, j)`.
- Limita o valor no intervalo de cinza.

### `aplicar_entre_imagens(matriz_a, matriz_b, callback_pixel, limitar_saida=True)`

O que faz:

- Processa duas imagens de mesmo tamanho pixel a pixel.

Como calcula:

- Para cada pixel, chama `callback_pixel(a, b, i, j)`.
- Se `limitar_saida=True`, aplica clamp `0..255`.
- Armazena resultado arredondado.

## Classe `MotorConvolucao`

### `__init__(base_operacoes=None)`

O que faz:

- Define o objeto base com utilitarios de matriz e clamp.

Como calcula:

- Usa o objeto recebido ou instancia `BaseOperacoesImagem`.

### `_get_pixel(matriz, i, j)`

O que faz:

- Acesso interno de pixel com fundo zero fora da imagem.

Como calcula:

- Delega para `obter_pixel_com_fundo(..., fundo=0)`.

### `aplicar(matriz, kernel, fator=1.0, deslocamento=0.0, callback_pos=None, limitar_saida=True)`

O que faz:

- Executa convolucao 2D generica.

Como calcula:

1. Para cada pixel de saida `(i, j)`, soma produtos do kernel sobre a vizinhanca.
2. Calcula `valor = acc * fator + deslocamento`.
3. Opcionalmente aplica `callback_pos(valor, i, j)`.
4. Arredonda e limita (opcionalmente).

### `aplicar_janela(matriz, callback_janela, tamanho_janela=3)`

O que faz:

- Aplica operacao baseada em janela local (ex.: mediana).

Como calcula:

- Coleta os vizinhos na janela centrada em `(i, j)`.
- Chama `callback_janela(vizinhos, i, j)`.
- Arredonda e limita a saida.

---

## operacoes/filtros.py

## Classe `FiltrosImagem`

### `filtro_media(matriz)`

O que faz:

- Suaviza a imagem por media local 3x3.

Como calcula:

- Convolui com kernel de uns 3x3 e fator `1/9`.

### `filtro_mediana(matriz)`

O que faz:

- Remove ruido impulsivo usando mediana local.

Como calcula:

- Em cada janela 3x3, ordena os 9 valores e pega o central.

### `filtro_passa_alta(matriz)`

O que faz:

- Realca componentes de alta frequencia (bordas).

Como calcula:

- Convolucao com kernel:
  - `[0, -1, 0]`
  - `[-1, 4, -1]`
  - `[0, -1, 0]`

### `filtro_roberts(matriz)`

O que faz:

- Detecta bordas por gradiente de Roberts em eixo cruzado simples.

Como calcula:

- Usa diferencas locais:
  - `gx = z5 - z8`
  - `gy = z5 - z6`
- Resposta: `|gx| + |gy|` com clamp.

### `filtro_roberts_cruzado(matriz)`

O que faz:

- Detecta bordas por versao cruzada do operador de Roberts.

Como calcula:

- Diferencas diagonais:
  - `gx = z5 - z9`
  - `gy = z6 - z8`
- Resposta: `|gx| + |gy|` com clamp.

### `filtro_prewitt(matriz)`

O que faz:

Como calcula:

### `filtro_sobel(matriz)`

O que faz:

- Detecta bordas com operador de Sobel.

Como calcula:

- Convolui com `kx` e `ky` do Sobel.
- Magnitude aproximada por norma euclidiana: `sqrt(gx^2 + gy^2)`.

### `_gradiente(matriz, kx, ky, usar_raiz=False)`

O que faz:

- Funcao auxiliar para combinar gradientes X e Y.

Como calcula:

- Calcula `gx` e `gy` por convolucao sem clamp intermediario.
- Se `usar_raiz=False`, usa `|gx| + |gy|`.
- Se `usar_raiz=True`, usa `sqrt(gx^2 + gy^2)`.

### `filtro_alto_reforco(matriz, A=1.5)`

O que faz:

- Realca detalhes preservando conteudo de baixa frequencia.

Como calcula:

1. Gera imagem suavizada pela media.
2. Mascara de detalhes: `mascara = original - suavizada`.
3. Saida: `original + A * mascara`.

---

## operacoes/histograma.py

## Classe `HistogramaImagem`

### `calcular_histograma(matriz)`

O que faz:

- Conta frequencia de cada nivel de cinza (0 a 255).

Como calcula:

- Inicializa vetor de 256 posicoes com zero.
- Para cada pixel, incrementa a posicao correspondente.

### `equalizar_histograma(matriz)`

O que faz:

- Equaliza contraste pela CDF do histograma.

Como calcula:

1. Calcula histograma original.
2. Calcula CDF acumulada.
3. Encontra `cdf_min` (primeiro acumulado > 0).
4. Calcula mapa:
   - Se `denominador = total_pixels - cdf_min` for positivo:
     `map[i] = round(((cdf[i] - cdf_min) / denominador) * 255)`
   - Senao, preserva `map[i] = i`.
5. Aplica o mapa na imagem para obter a equalizada.
6. Retorna matriz equalizada, histograma original, histograma equalizado e mapa.

---

## operacoes/morfismo.py

## Classe `MorfismoImagem`

### `interpolar_morfismo(matriz_a, matriz_b, t=0.5)`

O que faz:

- Interpola duas imagens de mesma dimensao.

Como calcula:

- Limita `t` no intervalo `[0, 1]`.
- Para cada pixel: `s = (1 - t) * A + t * B`.
- Arredonda e limita `0..255`.

---

## operacoes/morfologia.py

## Classe `BaseMorfologiaImagem`

### `binarizar(matriz, limiar=127)`

O que faz:

- Converte tons de cinza para binaria.

Como calcula:

- Pixel vira `255` se `>= limiar`, senao `0`.

### `gerar_elemento_estruturante_quadrado(tamanho=20)`

O que faz:

- Gera elemento estruturante quadrado binario.

Como calcula:

- Garante tamanho impar e no maximo 3.
- Retorna matriz `tamanho x tamanho` de uns.

### `parsear_elemento_estruturante(texto)`

O que faz:

- Interpreta elemento estruturante textual com `0`, `1` e `+1` (origem).

Como calcula:

- Separa linhas/colunas.
- Valida formato, dimensoes maximas (3x3) e consistencia.
- Converte `+1` em valor ativo e marca origem.
- Se nao houver origem explicita, usa centro geometrico.

### `_normalizar_elemento_estruturante(elemento_estruturante)`

O que faz:

- Normaliza elemento estruturante em formato textual ou matriz.

Como calcula:

- Se string, chama parser.
- Se matriz, valida e converte valores para mascara binaria + origem.

### `_aplicar_elemento(matriz, elemento_estruturante, valor_borda, callback_vizinhanca)`

O que faz:

- Motor generico para aplicar operacoes morfologicas por vizinhanca ativa.

Como calcula:

- Para cada pixel, coleta vizinhos onde a mascara eh ativa.
- Usa `valor_borda` para pontos fora da imagem.
- Chama `callback_vizinhanca(vizinhos)`.

### `_subtrair_matrizes(matriz_a, matriz_b)`

O que faz:

- Subtrai duas matrizes pixel a pixel.

Como calcula:

- Usa funcao de duas imagens com `a - b`.

## Classe `MorfologiaBinariaImagem`

### `_matriz_binaria_para_conjunto(matriz_binaria)`

O que faz:

- Converte imagem binaria para conjunto de coordenadas ativas.

Como calcula:

- Adiciona `(i, j)` quando pixel for diferente de zero.

### `_conjunto_para_matriz_binaria(conjunto, altura, largura)`

O que faz:

- Converte conjunto de coordenadas para matriz binaria.

Como calcula:

- Inicializa zeros e marca `255` nas coordenadas validas.

### `_calcular_fator_escala_elemento(altura, largura)`

O que faz:

- Define fator de escala do elemento estruturante conforme tamanho da imagem.

Como calcula:

- `fator = max(1, min(7, min(altura, largura) // 80))`.

### `_escalar_mascara_elemento(mascara, origem, fator)`

O que faz:

- Amplia mascara binaria do elemento estruturante.

Como calcula:

- Replica cada celula ativa em bloco `fator x fator`.
- Recalcula origem para centro da celula escalada.

### `_obter_offsets_elemento_estruturante(elemento_estruturante, altura, largura)`

O que faz:

- Gera offsets ativos relativos a origem do elemento.

Como calcula:

- Normaliza elemento, escala e coleta deslocamentos `(di, dj)`.

### `_dilatacao_binaria_conjunto(conjunto_a, offsets_b, altura, largura)`

O que faz:

- Dilatacao binaria em representacao por conjuntos.

Como calcula:

- Para cada pixel ativo de `A`, soma cada offset de `B`.
- Mantem apenas pontos dentro dos limites.

### `_erosao_binaria_conjunto(conjunto_a, offsets_b, altura, largura)`

O que faz:

- Erosao binaria em representacao por conjuntos.

Como calcula:

- Para cada ponto candidato `(i,j)`, exige que todos os offsets caiam em pixels ativos de `A`.

### `dilatacao_binaria(matriz_binaria, elemento_estruturante)`

O que faz:

- Dilatacao da imagem binaria.

Como calcula:

- Converte para conjunto, aplica dilatacao e reconverte para matriz.

### `erosao_binaria(matriz_binaria, elemento_estruturante)`

O que faz:

- Erosao da imagem binaria.

Como calcula:

- Converte para conjunto, aplica erosao e reconverte.

### `abertura_binaria(matriz_binaria, elemento_estruturante)`

O que faz:

- Abertura binaria (erosao seguida de dilatacao).

Como calcula:

- `aberta = dilatacao(erosao(A, B), B)`.

### `fechamento_binaria(matriz_binaria, elemento_estruturante)`

O que faz:

- Fechamento binario (dilatacao seguida de erosao).

Como calcula:

- `fechada = erosao(dilatacao(A, B), B)`.

### `gradiente_binaria(matriz_binaria, elemento_estruturante)`

O que faz:

- Gradiente morfologico binario.

Como calcula:

- Diferenca de conjuntos: `dilatada - erodida`.

### `contorno_externo_binaria(matriz_binaria, elemento_estruturante)`

O que faz:

- Extrai contorno externo binario.

Como calcula:

- Diferenca de conjuntos: `dilatada - original`.

### `contorno_interno_binaria(matriz_binaria, elemento_estruturante)`

O que faz:

- Extrai contorno interno binario.

Como calcula:

- Diferenca de conjuntos: `original - erodida`.

## Classe `MorfologiaCinzaImagem`

### `_gerar_elemento_estruturante_circular_flat()`

O que faz:

- Gera mascara circular plana (flat) para tons de cinza.

Como calcula:

- Usa equacao de circulo em grade discreta:
  `(i-c)^2 + (j-c)^2 <= raio^2`.

### `obter_texto_elemento_estruturante_circular_flat()`

O que faz:

- Retorna representacao textual da mascara circular.

Como calcula:

- Percorre mascara e marca centro com `+1`.

### `_aplicar_elemento_circular_flat(matriz, valor_borda, callback_vizinhanca)`

O que faz:

- Motor morfologico em tons de cinza com elemento circular fixo.

Como calcula:

- Coleta vizinhos ativos da mascara circular.
- Aplica callback de agregacao.

### `dilatacao_cinza(matriz, _elemento_estruturante=None)`

O que faz:

- Dilatacao em tons de cinza.

Como calcula:

- Usa maximo dos vizinhos ativos.

### `erosao_cinza(matriz, _elemento_estruturante=None)`

O que faz:

- Erosao em tons de cinza.

Como calcula:

- Usa minimo dos vizinhos ativos.

### `abertura_cinza(matriz, _elemento_estruturante=None)`

O que faz:

- Abertura em tons de cinza.

Como calcula:

- `dilatacao(erosao(matriz))`.

### `fechamento_cinza(matriz, _elemento_estruturante=None)`

O que faz:

- Fechamento em tons de cinza.

Como calcula:

- `erosao(dilatacao(matriz))`.

### `gradiente_cinza(matriz, _elemento_estruturante=None)`

O que faz:

- Gradiente morfologico em tons de cinza.

Como calcula:

- `dilatada - erodida`.

### `contorno_externo_cinza(matriz, _elemento_estruturante=None)`

O que faz:

- Contorno externo em tons de cinza.

Como calcula:

- `dilatada - original`.

### `contorno_interno_cinza(matriz, _elemento_estruturante=None)`

O que faz:

- Contorno interno em tons de cinza.

Como calcula:

- `original - erodida`.

---

## operacoes/operacoes_aritmeticas.py

## Classe `OperacoesAritmeticasImagem`

### `soma(matriz_a, matriz_b)`

O que faz:

- Soma duas imagens pixel a pixel.

Como calcula:

- `s = a + b` com clamp final `0..255`.

### `subtracao(matriz_a, matriz_b)`

O que faz:

- Subtrai imagem B da imagem A pixel a pixel.

Como calcula:

- `s = a - b` com clamp final.

### `multiplicacao(matriz_a, matriz_b)`

O que faz:

- Multiplica pixel a pixel.

Como calcula:

- `s = a * b` com clamp final.

### `divisao(matriz_a, matriz_b)`

O que faz:

- Divide pixel a pixel com protecao contra divisao por zero.

Como calcula:

- Se `b == 0`, usa `255`.
- Senao, `s = a / b`.
- Depois arredonda e aplica clamp.

---

## operacoes/operacoes_logicas.py

## Classe `OperacoesLogicasImagem`

### `_para_binario(valor)`

O que faz:

- Binariza um pixel por limiar.

Como calcula:

- Retorna `1` se `valor >= 127`, senao `0`.

### `_aplicar_binaria(matriz_a, matriz_b, callback_logica)`

O que faz:

- Aplica operacao logica binaria pixel a pixel.

Como calcula:

- Converte cada pixel de A e B para bit.
- Aplica callback (`AND`, `OR`, `XOR`).
- Mapeia `True` para `255` e `False` para `0`.

### `operacao_and(matriz_a, matriz_b)`

O que faz:

- AND logico binario entre duas imagens.

Como calcula:

- Usa `a & b` sobre os bits binarizados.

### `operacao_or(matriz_a, matriz_b)`

O que faz:

- OR logico binario entre duas imagens.

Como calcula:

- Usa `a | b` sobre os bits binarizados.

### `operacao_xor(matriz_a, matriz_b)`

O que faz:

- XOR logico binario entre duas imagens.

Como calcula:

- Usa `a ^ b` sobre os bits binarizados.

---

## operacoes/transformacoes_geometricas.py

## Classe `TransformacoesGeometricasImagem`

### `escala(matriz, fator_x=1.0, fator_y=None, valor_fundo=0)`

O que faz:

- Redimensiona imagem em X e Y com amostragem vizinho mais proximo.

Como calcula:

- Novo tamanho:
  - `nova_largura = round(largura * fator_x)`
  - `nova_altura = round(altura * fator_y)`
- Para cada pixel destino, busca origem por `int(i/fator_y)` e `int(j/fator_x)`.

### `translacao(matriz, deslocamento_x=0, deslocamento_y=0, valor_fundo=0)`

O que faz:

- Desloca imagem no plano.

Como calcula:

- Para pixel destino `(i,j)`, busca origem `(i-deslocamento_y, j-deslocamento_x)`.
- Fora da origem valida recebe `valor_fundo`.

### `rotacao(matriz, angulo_graus=0.0, valor_fundo=0)`

O que faz:

- Rotaciona a imagem mantendo todo o conteudo em novo canvas.

Como calcula:

1. Converte angulo para radianos e calcula `cos/sin`.
2. Roda os quatro cantos para obter bounds do novo canvas.
3. Para cada pixel destino, aplica transformacao inversa para achar origem.
4. Usa vizinho mais proximo com `round`.

### `reflexao(matriz, modo="horizontal")`

O que faz:

- Espelha imagem em horizontal, vertical ou diagonal.

Como calcula:

- Horizontal: inverte colunas.
- Vertical: inverte linhas.
- Diagonal: transpõe matriz.

### `cisalhamento(matriz, fator_x=0.0, fator_y=0.0, valor_fundo=0)`

O que faz:

- Aplica shear em X e/ou Y.

Como calcula:

1. Matriz de cisalhamento com determinante `1 - fator_x*fator_y`.
2. Valida invertibilidade (`det != 0`).
3. Determina bounds do novo canvas transformando cantos.
4. Para cada pixel destino, aplica transformacao inversa e amostra por vizinho mais proximo.

---

## operacoes/transformacoes_intensidade.py

## Classe `TransformacoesIntensidadeImagem`

### `negativo(matriz)`

O que faz:

- Inverte tons de cinza.

Como calcula:

- `s = 255 - r` para cada pixel.

### `transformacao_gamma(matriz, c=1.0, gamma=1.0)`

O que faz:

- Ajusta brilho/contraste por potencia (gamma).

Como calcula:

- Normaliza: `r_n = r / 255`.
- Aplica: `s_n = c * (r_n ^ gamma)`.
- Reescala: `s = round(s_n * 255)` com clamp.

### `transformacao_logaritmica(matriz, a=45.0)`

O que faz:

- Comprime faixa dinamica alta e expande baixa.

Como calcula:

- `s = a * log(r + 1)` com clamp.

### `funcao_janela(matriz, w=128.0, largura=80.0)`

O que faz:

- Windowing linear centrado em `w` com largura definida.

Como calcula:

- Define limites: `min = w - largura/2`, `max = w + largura/2`.
- Se `r <= min`, saida `0`.
- Se `r >= max`, saida `255`.
- No intervalo, interpolacao linear para `0..255`.

### `faixa_dinamica(matriz, r_min=0.0, r_max=255.0)`

O que faz:

- Expande/comprime linearmente uma faixa de entrada para `0..255`.

Como calcula:

- Se `r <= r_min`, saida `0`.
- Se `r >= r_max`, saida `255`.
- No intervalo, `s = ((r-r_min)/(r_max-r_min))*255`.

### `linear(matriz, alpha=1.0, beta=0.0)`

O que faz:

- Transformacao linear de intensidade.

Como calcula:

- `s = alpha * r + beta` com clamp.

---

## utils/leitura_pgm.py

### `ler_pgm(caminho_arquivo)`

O que faz:

- Le arquivo PGM ASCII (`P2`) e devolve matriz de pixels em 8 bits.

Como calcula:

1. Le linhas ignorando vazias e comentarios `#`.
2. Extrai tokens de cabecalho: formato, largura, altura, maximo.
3. Valida consistencia dos dados.
4. Le os pixels e clampa para `0..maximo_original`.
5. Normaliza para 8 bits:
   - `valor_8bits = round((valor_lido / maximo_original) * 255)`.
6. Retorna `(matriz, 255)`.

---

## scripts/gerar_imagem.py

### `salvar_pgm(nome_arquivo, matriz)`

O que faz:

- Salva matriz `numpy` em arquivo PGM ASCII.

Como calcula:

- Escreve cabecalho `P2`, dimensoes e maximo `255`.
- Escreve valores linha a linha.

### `gerar_forma_base()`

O que faz:

- Retorna matriz binaria pequena com forma base predefinida.

Como calcula:

- Retorna `numpy.array` fixo de `0` e `1`.

### `escalar_forma(forma, nova_altura, nova_largura)`

O que faz:

- Escala forma binaria por vizinho mais proximo.

Como calcula:

- Para cada pixel destino `(i,j)`:
  - `y = int(i * altura_original / nova_altura)`
  - `x = int(j * largura_original / nova_largura)`
- Copia `forma[y, x]`.

### `gerar_imagem_final(largura=100, altura=100)`

O que faz:

- Gera imagem final com forma escalada e centralizada.

Como calcula:

1. Cria fundo preto.
2. Escala forma base para ~70% da dimensao alvo.
3. Calcula deslocamento central:
   - `x_inicio = (largura - nova_largura)//2`
   - `y_inicio = (altura - nova_altura)//2`
4. Copia pixels ativos para valor `255`.

### Bloco principal (`if __name__ == "__main__":`)

O que faz:

- Gera exemplo e salva arquivo `forma_centrada.pgm`.

Como calcula:

- Chama `gerar_imagem_final(...)`.
- Chama `salvar_pgm(...)`.
- Exibe mensagem de sucesso.

---

## Observacoes finais

- Todas as operacoes de intensidade trabalham no dominio de 8 bits (`0..255`).
- Sempre que necessario, o projeto utiliza clamp para manter valores validos de pixel.
- Operacoes entre duas imagens exigem dimensoes iguais.
