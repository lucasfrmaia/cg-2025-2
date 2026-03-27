import tkinter as tk
from tkinter import filedialog, messagebox, ttk

from operacoes.filtros import FiltrosImagem
from operacoes.histograma import HistogramaImagem
from operacoes.morfismo import MorfismoImagem
from operacoes.morfologia import MorfologiaBinariaImagem, MorfologiaCinzaImagem
from operacoes.operacoes_aritmeticas import OperacoesAritmeticasImagem
from operacoes.operacoes_logicas import OperacoesLogicasImagem
from operacoes.transformacoes_geometricas import TransformacoesGeometricasImagem
from operacoes.transformacoes_intensidade import TransformacoesIntensidadeImagem
from utils.leitura_pgm import ler_pgm


class DefinicaoOperacao:
    def __init__(self, nome, executor, parametros=None, requer_segunda=False, exibe_histograma=False):
        self.nome = nome
        self.executor = executor
        self.parametros = parametros or []
        self.requer_segunda = requer_segunda
        self.exibe_histograma = exibe_histograma


class AplicacaoPDI:
    LARGURA_PAINEL_IMAGEM = 400
    ALTURA_PAINEL_IMAGEM = 300
    LARGURA_PAINEL_MATRIZ = 60
    ALTURA_PAINEL_MATRIZ = 50

    def __init__(self, janela):
        self.janela = janela
        self.janela.title("Laboratorio PDI - Processamento Digital de Imagens")
        self.janela.geometry("1320x820")
        self.janela.minsize(1040, 700)

        self.filtros = FiltrosImagem()
        self.histograma = HistogramaImagem()
        self.morfismo = MorfismoImagem()
        self.morfologia_binaria = MorfologiaBinariaImagem()
        self.morfologia_cinza = MorfologiaCinzaImagem()
        self.aritmetica = OperacoesAritmeticasImagem()
        self.logica = OperacoesLogicasImagem()
        self.geometria = TransformacoesGeometricasImagem()
        self.intensidade = TransformacoesIntensidadeImagem()

        self.matriz_a = None
        self.matriz_b = None
        self.matriz_resultado = None

        self.definicoes = self._criar_definicoes_operacoes()
        self.sessoes = self._criar_sessoes()

        self.questao_var = tk.StringVar()
        self.subsessao_var = tk.StringVar()
        self.operacao_var = tk.StringVar()

        self.entradas_parametros = []
        self.combos_parametros = []
        self.rotulos_parametros = []
        self.var_slider_morfismo = tk.DoubleVar(value=0.5)
        self.frame_slider_morfismo = None
        self.slider_morfismo = None
        self.rotulo_valor_slider_morfismo = None
        self.frame_elemento_estruturante = None
        self.entradas_elemento_estruturante = []
        self.frame_elemento_cinza_fixo = None
        self.texto_elemento_cinza_fixo = None

        self.painel_a = {}
        self.painel_b = {}
        self.painel_resultado = {}
        self.imagens_tk = {}

        self.canvas_principal = None
        self.frame_principal = None
        self._frame_scroll_id = None

        self._configurar_area_rolavel()

        self._montar_layout()
        self._inicializar_estado()

    def _configurar_area_rolavel(self):
        container = ttk.Frame(self.janela)
        container.pack(fill="both", expand=True)

        self.canvas_principal = tk.Canvas(container, highlightthickness=0)
        barra_vertical = ttk.Scrollbar(container, orient="vertical", command=self.canvas_principal.yview)
        barra_horizontal = ttk.Scrollbar(container, orient="horizontal", command=self.canvas_principal.xview)
        self.canvas_principal.configure(yscrollcommand=barra_vertical.set, xscrollcommand=barra_horizontal.set)

        barra_vertical.pack(side="right", fill="y")
        barra_horizontal.pack(side="bottom", fill="x")
        self.canvas_principal.pack(side="left", fill="both", expand=True)

        self.frame_principal = ttk.Frame(self.canvas_principal)
        self._frame_scroll_id = self.canvas_principal.create_window((0, 0), window=self.frame_principal, anchor="nw")

        self.frame_principal.bind("<Configure>", self._ao_configurar_frame_rolavel)
        self.canvas_principal.bind("<Configure>", self._ao_configurar_canvas_rolavel)
        self.canvas_principal.bind_all("<MouseWheel>", self._ao_rolar_mouse)
        self.canvas_principal.bind_all("<Shift-MouseWheel>", self._ao_rolar_mouse)

    def _ao_configurar_frame_rolavel(self, _evento=None):
        self.canvas_principal.configure(scrollregion=self.canvas_principal.bbox("all"))

    def _ao_configurar_canvas_rolavel(self, evento):
        largura_requisitada = self.frame_principal.winfo_reqwidth()
        self.canvas_principal.itemconfigure(self._frame_scroll_id, width=max(evento.width, largura_requisitada))

    def _ao_rolar_mouse(self, evento):
        if evento.delta:
            deslocamento = int(-evento.delta / 120)
            if evento.state & 0x0001:
                self.canvas_principal.xview_scroll(deslocamento, "units")
            else:
                self.canvas_principal.yview_scroll(deslocamento, "units")

    def _criar_definicoes_operacoes(self):
        def retorno(matriz, hist_original=None, hist_equalizado=None):
            return {
                "matriz": matriz,
                "hist_original": hist_original,
                "hist_equalizado": hist_equalizado,
            }

        def copia_matriz(matriz):
            return [linha[:] for linha in matriz]

        operacoes = {}

        operacoes["Filtro - Media"] = DefinicaoOperacao(
            "Filtro - Media",
            lambda a, _b, _p: retorno(self.filtros.filtro_media(a)),
        )
        operacoes["Filtro - Mediana"] = DefinicaoOperacao(
            "Filtro - Mediana",
            lambda a, _b, _p: retorno(self.filtros.filtro_mediana(a)),
        )
        operacoes["Filtro - Passa-alta"] = DefinicaoOperacao(
            "Filtro - Passa-alta",
            lambda a, _b, _p: retorno(self.filtros.filtro_passa_alta(a)),
        )
        operacoes["Filtro - Roberts"] = DefinicaoOperacao(
            "Filtro - Roberts",
            lambda a, _b, _p: retorno(self.filtros.filtro_roberts(a)),
        )
        operacoes["Filtro - Roberts cruzado"] = DefinicaoOperacao(
            "Filtro - Roberts cruzado",
            lambda a, _b, _p: retorno(self.filtros.filtro_roberts_cruzado(a)),
        )
        operacoes["Filtro - Prewitt"] = DefinicaoOperacao(
            "Filtro - Prewitt",
            lambda a, _b, _p: retorno(self.filtros.filtro_prewitt(a)),
        )
        operacoes["Filtro - Sobel"] = DefinicaoOperacao(
            "Filtro - Sobel",
            lambda a, _b, _p: retorno(self.filtros.filtro_sobel(a)),
        )
        operacoes["Filtro - Alto reforco"] = DefinicaoOperacao(
            "Filtro - Alto reforco",
            lambda a, _b, p: retorno(self.filtros.filtro_alto_reforco(a, p[0])),
            parametros=[
                {"rotulo": "Fator reforco", "padrao": "1.5", "tipo": float},
            ],
        )

        operacoes["Aritmetica - Soma"] = DefinicaoOperacao(
            "Aritmetica - Soma",
            lambda a, b, _p: retorno(self.aritmetica.soma(a, b)),
            requer_segunda=True,
        )
        operacoes["Aritmetica - Subtracao"] = DefinicaoOperacao(
            "Aritmetica - Subtracao",
            lambda a, b, _p: retorno(self.aritmetica.subtracao(a, b)),
            requer_segunda=True,
        )
        operacoes["Aritmetica - Multiplicacao"] = DefinicaoOperacao(
            "Aritmetica - Multiplicacao",
            lambda a, b, _p: retorno(self.aritmetica.multiplicacao(a, b)),
            requer_segunda=True,
        )
        operacoes["Aritmetica - Divisao"] = DefinicaoOperacao(
            "Aritmetica - Divisao",
            lambda a, b, _p: retorno(self.aritmetica.divisao(a, b)),
            requer_segunda=True,
        )

        operacoes["Logica - AND"] = DefinicaoOperacao(
            "Logica - AND",
            lambda a, b, p: retorno(self.logica.operacao_and(a, b, p[0])),
            parametros=[{"rotulo": "Limiar binario", "padrao": "127", "tipo": int}],
            requer_segunda=True,
        )
        operacoes["Logica - OR"] = DefinicaoOperacao(
            "Logica - OR",
            lambda a, b, p: retorno(self.logica.operacao_or(a, b, p[0])),
            parametros=[{"rotulo": "Limiar binario", "padrao": "127", "tipo": int}],
            requer_segunda=True,
        )
        operacoes["Logica - XOR"] = DefinicaoOperacao(
            "Logica - XOR",
            lambda a, b, p: retorno(self.logica.operacao_xor(a, b, p[0])),
            parametros=[{"rotulo": "Limiar binario", "padrao": "127", "tipo": int}],
            requer_segunda=True,
        )

        operacoes["Morfismo (interpolacao)"] = DefinicaoOperacao(
            "Morfismo (interpolacao)",
            lambda a, b, p: retorno(self.morfismo.interpolar_morfismo(a, b, p[0])),
            parametros=[{"rotulo": "t (0 a 1)", "padrao": "0.5", "tipo": float}],
            requer_segunda=True,
        )

        operacoes["Intensidade - Negativo"] = DefinicaoOperacao(
            "Intensidade - Negativo",
            lambda a, _b, _p: retorno(self.intensidade.negativo(a)),
        )
        operacoes["Intensidade - Gamma"] = DefinicaoOperacao(
            "Intensidade - Gamma",
            lambda a, _b, p: retorno(self.intensidade.transformacao_gamma(a, p[0], p[1])),
            parametros=[
                {"rotulo": "Constante c", "padrao": "1.0", "tipo": float},
                {"rotulo": "Gamma", "padrao": "1.0", "tipo": float},
            ],
        )
        operacoes["Intensidade - Logaritmo"] = DefinicaoOperacao(
            "Intensidade - Logaritmo",
            lambda a, _b, p: retorno(self.intensidade.transformacao_logaritmica(a, p[0])),
            parametros=[{"rotulo": "Constante a", "padrao": "45.0", "tipo": float}],
        )
        operacoes["Intensidade - Janela"] = DefinicaoOperacao(
            "Intensidade - Janela",
            lambda a, _b, p: retorno(self.intensidade.funcao_janela(a, p[0], p[1])),
            parametros=[
                {"rotulo": "Centro w", "padrao": "128", "tipo": float},
                {"rotulo": "Largura a", "padrao": "80", "tipo": float},
            ],
        )
        operacoes["Intensidade - Faixa dinamica"] = DefinicaoOperacao(
            "Intensidade - Faixa dinamica",
            lambda a, _b, p: retorno(self.intensidade.faixa_dinamica(a, p[0], p[1])),
            parametros=[
                {"rotulo": "r_min", "padrao": "50", "tipo": float},
                {"rotulo": "r_max", "padrao": "200", "tipo": float},
            ],
        )
        operacoes["Intensidade - Linear"] = DefinicaoOperacao(
            "Intensidade - Linear",
            lambda a, _b, p: retorno(self.intensidade.linear(a, p[0], p[1])),
            parametros=[
                {"rotulo": "Alpha", "padrao": "1.2", "tipo": float},
                {"rotulo": "Beta", "padrao": "0", "tipo": float},
            ],
        )

        operacoes["Histograma - Calcular"] = DefinicaoOperacao(
            "Histograma - Calcular",
            lambda a, _b, _p: retorno(copia_matriz(a), self.histograma.calcular_histograma(a), None),
            exibe_histograma=True,
        )
        operacoes["Histograma - Equalizar"] = DefinicaoOperacao(
            "Histograma - Equalizar",
            lambda a, _b, _p: self._executar_equalizacao_histograma(a),
            exibe_histograma=True,
        )

        padrao_elemento = "1 1 1; 1 +1 1; 1 1 1"
        operacoes["Morfologia binaria - Dilatacao"] = DefinicaoOperacao(
            "Morfologia binaria - Dilatacao",
            lambda a, _b, p: retorno(self._executar_morfologia_binaria(a, p, "dilatacao")),
            parametros=[
                {"rotulo": "Limiar", "padrao": "127", "tipo": int},
                {"rotulo": "Elemento estruturante (ate 3x3, +1=origem)", "padrao": padrao_elemento, "tipo": str},
            ],
        )
        operacoes["Morfologia binaria - Erosao"] = DefinicaoOperacao(
            "Morfologia binaria - Erosao",
            lambda a, _b, p: retorno(self._executar_morfologia_binaria(a, p, "erosao")),
            parametros=[
                {"rotulo": "Limiar", "padrao": "127", "tipo": int},
                {"rotulo": "Elemento estruturante (ate 3x3, +1=origem)", "padrao": padrao_elemento, "tipo": str},
            ],
        )
        operacoes["Morfologia binaria - Abertura"] = DefinicaoOperacao(
            "Morfologia binaria - Abertura",
            lambda a, _b, p: retorno(self._executar_morfologia_binaria(a, p, "abertura")),
            parametros=[
                {"rotulo": "Limiar", "padrao": "127", "tipo": int},
                {"rotulo": "Elemento estruturante (ate 3x3, +1=origem)", "padrao": padrao_elemento, "tipo": str},
            ],
        )
        operacoes["Morfologia binaria - Fechamento"] = DefinicaoOperacao(
            "Morfologia binaria - Fechamento",
            lambda a, _b, p: retorno(self._executar_morfologia_binaria(a, p, "fechamento")),
            parametros=[
                {"rotulo": "Limiar", "padrao": "127", "tipo": int},
                {"rotulo": "Elemento estruturante (ate 3x3, +1=origem)", "padrao": padrao_elemento, "tipo": str},
            ],
        )
        operacoes["Morfologia binaria - Gradiente"] = DefinicaoOperacao(
            "Morfologia binaria - Gradiente",
            lambda a, _b, p: retorno(self._executar_morfologia_binaria(a, p, "gradiente")),
            parametros=[
                {"rotulo": "Limiar", "padrao": "127", "tipo": int},
                {"rotulo": "Elemento estruturante (ate 3x3, +1=origem)", "padrao": padrao_elemento, "tipo": str},
            ],
        )
        operacoes["Morfologia binaria - Contorno externo"] = DefinicaoOperacao(
            "Morfologia binaria - Contorno externo",
            lambda a, _b, p: retorno(self._executar_morfologia_binaria(a, p, "contorno_externo")),
            parametros=[
                {"rotulo": "Limiar", "padrao": "127", "tipo": int},
                {"rotulo": "Elemento estruturante (ate 3x3, +1=origem)", "padrao": padrao_elemento, "tipo": str},
            ],
        )
        operacoes["Morfologia binaria - Contorno interno"] = DefinicaoOperacao(
            "Morfologia binaria - Contorno interno",
            lambda a, _b, p: retorno(self._executar_morfologia_binaria(a, p, "contorno_interno")),
            parametros=[
                {"rotulo": "Limiar", "padrao": "127", "tipo": int},
                {"rotulo": "Elemento estruturante (ate 3x3, +1=origem)", "padrao": padrao_elemento, "tipo": str},
            ],
        )

        operacoes["Morfologia cinza - Dilatacao"] = DefinicaoOperacao(
            "Morfologia cinza - Dilatacao",
            lambda a, _b, _p: retorno(self._executar_morfologia_cinza(a, "dilatacao")),
        )
        operacoes["Morfologia cinza - Erosao"] = DefinicaoOperacao(
            "Morfologia cinza - Erosao",
            lambda a, _b, _p: retorno(self._executar_morfologia_cinza(a, "erosao")),
        )
        operacoes["Morfologia cinza - Abertura"] = DefinicaoOperacao(
            "Morfologia cinza - Abertura",
            lambda a, _b, _p: retorno(self._executar_morfologia_cinza(a, "abertura")),
        )
        operacoes["Morfologia cinza - Fechamento"] = DefinicaoOperacao(
            "Morfologia cinza - Fechamento",
            lambda a, _b, _p: retorno(self._executar_morfologia_cinza(a, "fechamento")),
        )
        operacoes["Morfologia cinza - Gradiente"] = DefinicaoOperacao(
            "Morfologia cinza - Gradiente",
            lambda a, _b, _p: retorno(self._executar_morfologia_cinza(a, "gradiente")),
        )
        operacoes["Morfologia cinza - Contorno externo"] = DefinicaoOperacao(
            "Morfologia cinza - Contorno externo",
            lambda a, _b, _p: retorno(self._executar_morfologia_cinza(a, "contorno_externo")),
        )
        operacoes["Morfologia cinza - Contorno interno"] = DefinicaoOperacao(
            "Morfologia cinza - Contorno interno",
            lambda a, _b, _p: retorno(self._executar_morfologia_cinza(a, "contorno_interno")),
        )

        operacoes["Geometrica - Escala"] = DefinicaoOperacao(
            "Geometrica - Escala",
            lambda a, _b, p: retorno(self.geometria.escala(a, p[0], p[1])),
            parametros=[
                {"rotulo": "Fator X", "padrao": "1.5", "tipo": float},
                {"rotulo": "Fator Y", "padrao": "1.5", "tipo": float},
            ],
        )
        operacoes["Geometrica - Translacao"] = DefinicaoOperacao(
            "Geometrica - Translacao",
            lambda a, _b, p: retorno(self.geometria.translacao(a, p[0], p[1])),
            parametros=[
                {"rotulo": "Deslocamento X", "padrao": "20", "tipo": int},
                {"rotulo": "Deslocamento Y", "padrao": "20", "tipo": int},
            ],
        )
        operacoes["Geometrica - Rotacao"] = DefinicaoOperacao(
            "Geometrica - Rotacao",
            lambda a, _b, p: retorno(self.geometria.rotacao(a, p[0])),
            parametros=[{"rotulo": "Angulo (graus)", "padrao": "30", "tipo": float}],
        )
        operacoes["Geometrica - Reflexao"] = DefinicaoOperacao(
            "Geometrica - Reflexao",
            lambda a, _b, p: retorno(self.geometria.reflexao(a, p[0].lower())),
            parametros=[
                {
                    "rotulo": "Tipo de reflexao",
                    "padrao": "horizontal",
                    "tipo": "select",
                    "opcoes": ["horizontal", "vertical", "diagonal"],
                }
            ],
        )
        operacoes["Geometrica - Cisalhamento"] = DefinicaoOperacao(
            "Geometrica - Cisalhamento",
            lambda a, _b, p: retorno(self.geometria.cisalhamento(a, p[0], p[1])),
            parametros=[
                {"rotulo": "Fator X", "padrao": "0.2", "tipo": float},
                {"rotulo": "Fator Y", "padrao": "0.0", "tipo": float},
            ],
        )

        return operacoes

    def _criar_sessoes(self):
        return {
            "Modulo 1 - Filtragem e Operacoes": {
                "descricao": "Tecnicas de filtragem espacial e operacoes entre duas imagens em tons de cinza.",
                "sub_sessoes": {
                    "Filtragem em imagem unica": [
                        "Filtro - Media",
                        "Filtro - Mediana",
                        "Filtro - Passa-alta",
                        "Filtro - Roberts",
                        "Filtro - Roberts cruzado",
                        "Filtro - Prewitt",
                        "Filtro - Sobel",
                        "Filtro - Alto reforco",
                    ],
                    "Operacoes entre duas imagens": [
                        "Aritmetica - Soma",
                        "Aritmetica - Subtracao",
                        "Aritmetica - Multiplicacao",
                        "Aritmetica - Divisao",
                        "Logica - AND",
                        "Logica - OR",
                        "Logica - XOR",
                    ],
                },
            },
            "Modulo 2 - Morfismo": {
                "descricao": "Interpolacao gradual entre imagem A e imagem B usando fator de transicao.",
                "operacoes": ["Morfismo (interpolacao)"],
            },
            "Modulo 3 - Transformacoes de Intensidade": {
                "descricao": "Ajustes de contraste e brilho com funcoes ponto a ponto sobre os niveis de cinza.",
                "operacoes": [
                    "Intensidade - Negativo",
                    "Intensidade - Gamma",
                    "Intensidade - Logaritmo",
                    "Intensidade - Janela",
                    "Intensidade - Faixa dinamica",
                    "Intensidade - Linear",
                ],
            },
            "Modulo 4 - Histograma": {
                "descricao": "Analise de distribuicao tonal e equalizacao para melhoria de contraste.",
                "operacoes": ["Histograma - Calcular", "Histograma - Equalizar"],
            },
            "Modulo 5 - Morfologia": {
                "descricao": "Operacoes morfologicas para imagem binaria e em tons de cinza com elemento estruturante customizado (ate 3x3, +1 define a origem).",
                "sub_sessoes": {
                    "Morfologia binaria": [
                        "Morfologia binaria - Dilatacao",
                        "Morfologia binaria - Erosao",
                        "Morfologia binaria - Abertura",
                        "Morfologia binaria - Fechamento",
                        "Morfologia binaria - Gradiente",
                        "Morfologia binaria - Contorno externo",
                        "Morfologia binaria - Contorno interno",
                    ],
                    "Morfologia em tons de cinza": [
                        "Morfologia cinza - Dilatacao",
                        "Morfologia cinza - Erosao",
                        "Morfologia cinza - Abertura",
                        "Morfologia cinza - Fechamento",
                        "Morfologia cinza - Gradiente",
                        "Morfologia cinza - Contorno externo",
                        "Morfologia cinza - Contorno interno",
                    ],
                },
            },
            "Modulo 6 - Transformacoes Geometricas": {
                "descricao": "Transformacoes de forma e posicao: escala, translacao, rotacao, reflexao e cisalhamento.",
                "operacoes": [
                    "Geometrica - Escala",
                    "Geometrica - Translacao",
                    "Geometrica - Rotacao",
                    "Geometrica - Reflexao",
                    "Geometrica - Cisalhamento",
                ],
            },
        }

    def _montar_layout(self):
        self._montar_topo_sessoes()
        self._montar_controles_arquivos()
        self._montar_parametros()
        self._montar_paineis_imagem()
        self._montar_histograma()

    def _montar_topo_sessoes(self):
        quadro = ttk.LabelFrame(self.frame_principal, text="Painel de Selecao de Atividades")
        quadro.pack(fill="x", padx=10, pady=8)

        ttk.Label(quadro, text="Modulo:").grid(row=0, column=0, padx=6, pady=6, sticky="w")
        self.combo_questao = ttk.Combobox(quadro, textvariable=self.questao_var, state="readonly", width=38)
        self.combo_questao.grid(row=0, column=1, padx=6, pady=6, sticky="w")
        self.combo_questao.bind("<<ComboboxSelected>>", self._ao_mudar_questao)

        self.rotulo_subsessao = ttk.Label(quadro, text="Submodulo:")
        self.rotulo_subsessao.grid(row=0, column=2, padx=6, pady=6, sticky="w")

        self.combo_subsessao = ttk.Combobox(quadro, textvariable=self.subsessao_var, state="readonly", width=34)
        self.combo_subsessao.grid(row=0, column=3, padx=6, pady=6, sticky="w")
        self.combo_subsessao.bind("<<ComboboxSelected>>", self._ao_mudar_subsessao)

        ttk.Label(quadro, text="Operacao:").grid(row=1, column=0, padx=6, pady=6, sticky="w")
        self.combo_operacao = ttk.Combobox(quadro, textvariable=self.operacao_var, state="readonly", width=38)
        self.combo_operacao.grid(row=1, column=1, padx=6, pady=6, sticky="w")
        self.combo_operacao.bind("<<ComboboxSelected>>", self._ao_mudar_operacao)

        self.botao_aplicar = ttk.Button(quadro, text="Executar operacao", command=self.aplicar_operacao)
        self.botao_aplicar.grid(row=1, column=2, padx=6, pady=6, sticky="w")

        self.rotulo_descricao_sessao = ttk.Label(quadro, text="")
        self.rotulo_descricao_sessao.grid(row=2, column=0, columnspan=4, padx=6, pady=(2, 8), sticky="w")

    def _montar_controles_arquivos(self):
        quadro = ttk.LabelFrame(self.frame_principal, text="Carregamento de Imagens")
        quadro.pack(fill="x", padx=10, pady=6)

        self.botao_a = ttk.Button(quadro, text="Carregar imagem A (entrada principal)", command=self.carregar_imagem_a)
        self.botao_a.grid(row=0, column=0, padx=6, pady=6, sticky="w")

        self.botao_b = ttk.Button(quadro, text="Carregar imagem B (quando necessario)", command=self.carregar_imagem_b)
        self.botao_b.grid(row=0, column=1, padx=6, pady=6, sticky="w")

        self.info_a = ttk.Label(quadro, text="Imagem A: nenhuma imagem carregada")
        self.info_a.grid(row=1, column=0, padx=6, pady=4, sticky="w")

        self.info_b = ttk.Label(quadro, text="Imagem B: nenhuma imagem carregada")
        self.info_b.grid(row=1, column=1, padx=6, pady=4, sticky="w")

        self.info_c = ttk.Label(quadro, text="Imagem C (resultado): sera gerada ao executar a operacao")
        self.info_c.grid(row=2, column=0, columnspan=2, padx=6, pady=(2, 6), sticky="w")

    def _montar_parametros(self):
        quadro = ttk.LabelFrame(self.frame_principal, text="Parametros da Operacao Selecionada")
        quadro.pack(fill="x", padx=10, pady=6)

        for indice in range(3):
            coluna_base = indice * 2
            rotulo = ttk.Label(quadro, text=f"Parametro {indice + 1}")
            entrada = ttk.Entry(quadro, width=18)
            combo = ttk.Combobox(quadro, state="readonly", width=18)

            rotulo.grid(row=0, column=coluna_base, padx=6, pady=6, sticky="w")
            entrada.grid(row=0, column=coluna_base + 1, padx=6, pady=6, sticky="w")
            combo.grid(row=0, column=coluna_base + 1, padx=6, pady=6, sticky="w")
            combo.grid_remove()

            self.rotulos_parametros.append(rotulo)
            self.entradas_parametros.append(entrada)
            self.combos_parametros.append(combo)

        self.frame_elemento_estruturante = ttk.Frame(quadro)
        ttk.Label(
            self.frame_elemento_estruturante,
            text="Elemento estruturante 3x3 (use 0, 1 e +1 para origem):",
        ).grid(row=0, column=0, columnspan=3, padx=6, pady=(0, 4), sticky="w")

        grade = ttk.Frame(self.frame_elemento_estruturante)
        grade.grid(row=1, column=0, padx=6, pady=(0, 6), sticky="w")

        for i in range(3):
            linha = []
            for j in range(3):
                entrada = ttk.Entry(grade, width=4, justify="center")
                entrada.grid(row=i, column=j, padx=2, pady=2)
                linha.append(entrada)
            self.entradas_elemento_estruturante.append(linha)

        self._preencher_elemento_estruturante_padrao()
        self.frame_elemento_estruturante.grid(row=1, column=0, columnspan=6, sticky="w")
        self.frame_elemento_estruturante.grid_remove()

        self.frame_elemento_cinza_fixo = ttk.Frame(quadro)
        ttk.Label(
            self.frame_elemento_cinza_fixo,
            text="Elemento estruturante fixo (tons de cinza): circulo flat",
        ).grid(row=0, column=0, padx=6, pady=(0, 4), sticky="w")

        self.texto_elemento_cinza_fixo = tk.Text(
            self.frame_elemento_cinza_fixo,
            width=18,
            height=6,
            wrap="none",
        )
        self.texto_elemento_cinza_fixo.grid(row=1, column=0, padx=6, pady=(0, 6), sticky="w")
        self.texto_elemento_cinza_fixo.insert("1.0", self.morfologia_cinza.obter_texto_elemento_estruturante_circular_flat())
        self.texto_elemento_cinza_fixo.configure(state="disabled")
        self.frame_elemento_cinza_fixo.grid(row=2, column=0, columnspan=6, sticky="w")
        self.frame_elemento_cinza_fixo.grid_remove()

        self.frame_slider_morfismo = ttk.Frame(quadro)
        ttk.Label(self.frame_slider_morfismo, text="Morfismo em tempo real (t):").grid(
            row=0,
            column=0,
            padx=(6, 4),
            pady=(0, 6),
            sticky="w",
        )

        self.slider_morfismo = ttk.Scale(
            self.frame_slider_morfismo,
            from_=0.0,
            to=1.0,
            variable=self.var_slider_morfismo,
            command=self._ao_mover_slider_morfismo,
        )
        self.slider_morfismo.grid(row=0, column=1, padx=4, pady=(0, 6), sticky="ew")

        self.rotulo_valor_slider_morfismo = ttk.Label(self.frame_slider_morfismo, text="0.50")
        self.rotulo_valor_slider_morfismo.grid(row=0, column=2, padx=(4, 6), pady=(0, 6), sticky="w")

        self.frame_slider_morfismo.grid_columnconfigure(1, weight=1)
        self.frame_slider_morfismo.grid(row=3, column=0, columnspan=6, sticky="ew")
        self.frame_slider_morfismo.grid_remove()

    def _montar_paineis_imagem(self):
        quadro = ttk.LabelFrame(self.frame_principal, text="Visualizacao das Imagens e Matrizes")
        quadro.pack(fill="x", padx=10, pady=8)

        self.container_paineis = ttk.Frame(quadro)
        self.container_paineis.pack(fill="x", anchor="w", padx=6, pady=6)

        self.painel_a = self._criar_painel_imagem(self.container_paineis, "Imagem A (entrada principal)", "A")
        self.painel_b = self._criar_painel_imagem(self.container_paineis, "Imagem B (entrada auxiliar)", "B")
        self.painel_resultado = self._criar_painel_imagem(self.container_paineis, "Imagem C (resultado da operacao)", "R")

        self._organizar_paineis_imagem(mostrar_b=False)

    def _criar_painel_imagem(self, container, titulo, chave):
        quadro = ttk.LabelFrame(container, text=titulo)
        canvas = tk.Canvas(
            quadro,
            width=self.LARGURA_PAINEL_IMAGEM,
            height=self.ALTURA_PAINEL_IMAGEM,
            bg="white",
        )
        area_texto = ttk.Frame(quadro, width=self.LARGURA_PAINEL_MATRIZ, height=self.ALTURA_PAINEL_MATRIZ)
        area_texto.pack_propagate(False)

        texto = tk.Text(area_texto, wrap="none")
        texto.bind("<Button-1>", lambda evento, c=chave: self._ao_clicar_pixel_matriz(evento, c))
        barra_texto_y = ttk.Scrollbar(area_texto, orient="vertical", command=texto.yview)
        barra_texto_x = ttk.Scrollbar(area_texto, orient="horizontal", command=texto.xview)
        texto.configure(yscrollcommand=barra_texto_y.set, xscrollcommand=barra_texto_x.set)

        area_texto.grid_rowconfigure(0, weight=1)
        area_texto.grid_columnconfigure(0, weight=1)
        texto.grid(row=0, column=0, sticky="nsew")
        barra_texto_y.grid(row=0, column=1, sticky="ns")
        barra_texto_x.grid(row=1, column=0, sticky="ew")

        info = ttk.Label(quadro, text="")

        info.pack(anchor="w", padx=8, pady=(6, 0))
        canvas.pack(padx=8, pady=6)
        area_texto.pack(padx=8, pady=(0, 8))

        self.imagens_tk[chave] = None

        return {
            "frame": quadro,
            "canvas": canvas,
            "texto": texto,
            "info": info,
            "chave": chave,
            "matriz": None,
        }

    def _montar_histograma(self):
        quadro = ttk.LabelFrame(self.frame_principal, text="Analise de Histogramas (Modulo 4)")
        quadro.pack(fill="x", padx=10, pady=(0, 10))

        self.container_histograma = quadro

        interno = ttk.Frame(quadro)
        interno.pack(fill="x", padx=8, pady=8)

        bloco_o = ttk.Frame(interno)
        bloco_o.pack(fill="x", expand=True, padx=0, pady=(0, 8), anchor="w")

        ttk.Label(bloco_o, text="Histograma original").pack(anchor="w")
        self.canvas_hist_original = tk.Canvas(bloco_o, width=760, height=300, bg="white")
        self.canvas_hist_original.pack(fill="x", pady=(4, 0))

        bloco_e = ttk.Frame(interno)
        bloco_e.pack(fill="x", expand=True, padx=0, pady=(8, 0), anchor="w")

        ttk.Label(bloco_e, text="Histograma equalizado").pack(anchor="w")
        self.canvas_hist_equalizado = tk.Canvas(bloco_e, width=760, height=300, bg="white")
        self.canvas_hist_equalizado.pack(fill="x", pady=(4, 0))

    def _inicializar_estado(self):
        questoes = list(self.sessoes.keys())
        self.combo_questao.configure(values=questoes)
        if questoes:
            self.questao_var.set(questoes[0])

        self._atualizar_sessao_ativa()

    def _ao_mudar_questao(self, _evento=None):
        self._resetar_estado_inicial_tela()
        self._atualizar_sessao_ativa()

    def _ao_mudar_subsessao(self, _evento=None):
        self._resetar_estado_inicial_tela()
        self._atualizar_operacoes_sessao()

    def _ao_mudar_operacao(self, _evento=None):
        self._atualizar_parametros_operacao()

    def _atualizar_sessao_ativa(self):
        nome_questao = self.questao_var.get()
        dados = self.sessoes[nome_questao]

        self.rotulo_descricao_sessao.configure(text=dados["descricao"])

        if "sub_sessoes" in dados:
            nomes_sub = list(dados["sub_sessoes"].keys())
            self.combo_subsessao.configure(values=nomes_sub, state="readonly")
            self.subsessao_var.set(nomes_sub[0])
            self.rotulo_subsessao.grid()
            self.combo_subsessao.grid()
        else:
            self.subsessao_var.set("")
            self.combo_subsessao.set("")
            self.combo_subsessao.configure(values=[], state="disabled")
            self.rotulo_subsessao.grid_remove()
            self.combo_subsessao.grid_remove()

        self._atualizar_operacoes_sessao()

    def _obter_operacoes_sessao_atual(self):
        dados = self.sessoes[self.questao_var.get()]

        if "sub_sessoes" in dados:
            return dados["sub_sessoes"][self.subsessao_var.get()]

        return dados["operacoes"]

    def _atualizar_operacoes_sessao(self):
        operacoes = self._obter_operacoes_sessao_atual()
        self.combo_operacao.configure(values=operacoes)

        if operacoes:
            self.operacao_var.set(operacoes[0])
        else:
            self.operacao_var.set("")

        self._atualizar_parametros_operacao()

    def _atualizar_parametros_operacao(self):
        nome_operacao = self.operacao_var.get()
        if not nome_operacao:
            self._desabilitar_parametros()
            return

        definicao = self.definicoes[nome_operacao]
        parametros = definicao.parametros

        for indice in range(3):
            rotulo = self.rotulos_parametros[indice]
            entrada = self.entradas_parametros[indice]
            combo = self.combos_parametros[indice]

            if indice < len(parametros):
                rotulo.configure(text=parametros[indice]["rotulo"])
                meta = parametros[indice]
                if meta["tipo"] == "select":
                    self._configurar_entrada(entrada, "", habilitada=False, mostrar=False)
                    self._configurar_combo(combo, meta.get("opcoes", []), meta["padrao"], habilitada=True)
                else:
                    self._configurar_combo(combo, [], "", habilitada=False)
                    self._configurar_entrada(entrada, meta["padrao"], habilitada=True)
            else:
                rotulo.configure(text="")
                self._configurar_combo(combo, [], "", habilitada=False)
                self._configurar_entrada(entrada, "", habilitada=False)

        usa_elemento = self._operacao_usa_elemento_estruturante(nome_operacao)
        self._mostrar_elemento_estruturante(usa_elemento)
        usa_elemento_cinza_fixo = self._operacao_usa_elemento_cinza_fixo(nome_operacao)
        self._mostrar_elemento_cinza_fixo(usa_elemento_cinza_fixo)
        if usa_elemento:
            if nome_operacao.startswith("Morfologia binaria"):
                self.rotulos_parametros[1].configure(text="")
                self._configurar_entrada(self.entradas_parametros[1], "", habilitada=False)

        morfismo_ativo = nome_operacao == "Morfismo (interpolacao)"
        self._mostrar_slider_morfismo(morfismo_ativo)
        if morfismo_ativo and parametros:
            try:
                valor_inicial = float(parametros[0]["padrao"])
            except (KeyError, TypeError, ValueError):
                valor_inicial = 0.5
            self._sincronizar_slider_morfismo(valor_inicial, aplicar=False)

        self.botao_b.configure(state="normal" if definicao.requer_segunda else "disabled")

        if definicao.requer_segunda:
            self.painel_resultado["frame"].configure(text="Imagem C (resultado da operacao)")
            self.info_c.configure(text="Imagem C (resultado): execute a operacao entre A e B")
        else:
            self.painel_resultado["frame"].configure(text="Imagem Processada (resultado)")
            self.info_c.configure(text="Imagem Resultado: execute a operacao selecionada")

        self._organizar_paineis_imagem(mostrar_b=definicao.requer_segunda)

        if definicao.exibe_histograma:
            self.container_histograma.pack(fill="x", padx=10, pady=(0, 10))
        else:
            self.container_histograma.pack_forget()

    def _desabilitar_parametros(self):
        self._mostrar_slider_morfismo(False)
        self._mostrar_elemento_estruturante(False)
        self._mostrar_elemento_cinza_fixo(False)
        for indice in range(3):
            self.rotulos_parametros[indice].configure(text="")
            self._configurar_entrada(self.entradas_parametros[indice], "", habilitada=False)
            self._configurar_combo(self.combos_parametros[indice], [], "", habilitada=False)

    def _operacao_usa_elemento_estruturante(self, nome_operacao):
        return nome_operacao.startswith("Morfologia binaria")

    def _operacao_usa_elemento_cinza_fixo(self, nome_operacao):
        return nome_operacao.startswith("Morfologia cinza")

    def _mostrar_elemento_estruturante(self, mostrar):
        if self.frame_elemento_estruturante is None:
            return

        if mostrar:
            self.frame_elemento_estruturante.grid(row=1, column=0, columnspan=6, sticky="w")
        else:
            self.frame_elemento_estruturante.grid_remove()

    def _mostrar_elemento_cinza_fixo(self, mostrar):
        if self.frame_elemento_cinza_fixo is None:
            return

        if mostrar:
            self.frame_elemento_cinza_fixo.grid(row=2, column=0, columnspan=6, sticky="w")
        else:
            self.frame_elemento_cinza_fixo.grid_remove()

    def _preencher_elemento_estruturante_padrao(self):
        padrao = [
            ["1", "1", "1"],
            ["1", "+1", "1"],
            ["1", "1", "1"],
        ]

        for i in range(3):
            for j in range(3):
                entrada = self.entradas_elemento_estruturante[i][j]
                entrada.delete(0, tk.END)
                entrada.insert(0, padrao[i][j])

    def _normalizar_token_elemento(self, texto):
        token = (texto or "").strip()
        token = token.replace("[", "").replace("]", "").replace(";", "").replace(",", "")

        if token in {"", "0", "0.", ".0"}:
            return "0"
        if token in {"1", "1."}:
            return "1"
        if token in {"+1", "+1."}:
            return "+1"

        raise ValueError("Elemento estruturante aceita apenas 0, 1 e +1.")

    def _ler_elemento_estruturante_como_texto(self):
        matriz = []
        coordenadas_ativas = []

        linhas = []
        for i in range(3):
            valores = []
            for j in range(3):
                bruto = self.entradas_elemento_estruturante[i][j].get().strip()
                valor = self._normalizar_token_elemento(bruto)
                if valor in {"1", "+1"}:
                    coordenadas_ativas.append((i, j))
                valores.append(valor)
            matriz.append(valores)

        if not coordenadas_ativas:
            raise ValueError("Defina ao menos um valor 1 ou +1 no elemento estruturante.")

        min_i = min(i for i, _j in coordenadas_ativas)
        max_i = max(i for i, _j in coordenadas_ativas)
        min_j = min(j for _i, j in coordenadas_ativas)
        max_j = max(j for _i, j in coordenadas_ativas)

        for i in range(min_i, max_i + 1):
            linha = []
            for j in range(min_j, max_j + 1):
                linha.append(matriz[i][j])
            linhas.append(" ".join(linha))

        return "; ".join(linhas)

    def _limpar_painel(self, painel, texto_info):
        painel["matriz"] = None
        painel["info"].configure(text=texto_info)
        painel["texto"].delete("1.0", tk.END)
        painel["texto"].tag_remove("pixel_selecionado", "1.0", tk.END)
        painel["canvas"].delete("all")
        self.imagens_tk[painel["chave"]] = None

    def _resetar_estado_inicial_tela(self):
        self.matriz_a = None
        self.matriz_b = None
        self.matriz_resultado = None

        self.info_a.configure(text="Imagem A: nenhuma imagem carregada")
        self.info_b.configure(text="Imagem B: nenhuma imagem carregada")
        self.info_c.configure(text="Imagem C (resultado): sera gerada ao executar a operacao")

        self.painel_resultado["frame"].configure(text="Imagem C (resultado da operacao)")

        self._limpar_painel(self.painel_a, "")
        self._limpar_painel(self.painel_b, "")
        self._limpar_painel(self.painel_resultado, "")

        self.canvas_hist_original.delete("all")
        self.canvas_hist_equalizado.delete("all")

        self._preencher_elemento_estruturante_padrao()
        self._sincronizar_slider_morfismo(0.5, aplicar=False)

    def _mostrar_slider_morfismo(self, mostrar):
        if self.frame_slider_morfismo is None:
            return

        if mostrar:
            self.frame_slider_morfismo.grid(row=3, column=0, columnspan=6, sticky="ew")
        else:
            self.frame_slider_morfismo.grid_remove()

    def _sincronizar_slider_morfismo(self, valor, aplicar):
        if self.slider_morfismo is None:
            return

        valor = max(0.0, min(1.0, float(valor)))
        self.var_slider_morfismo.set(valor)
        self.rotulo_valor_slider_morfismo.configure(text=f"{valor:.2f}")

        if self.entradas_parametros:
            entrada = self.entradas_parametros[0]
            if str(entrada.cget("state")) != "disabled":
                entrada.delete(0, tk.END)
                entrada.insert(0, f"{valor:.2f}")

        if aplicar:
            self._executar_morfismo_em_tempo_real()

    def _ao_mover_slider_morfismo(self, _valor=None):
        if self.operacao_var.get() != "Morfismo (interpolacao)":
            return

        self._sincronizar_slider_morfismo(self.var_slider_morfismo.get(), aplicar=True)

    def _executar_morfismo_em_tempo_real(self):
        if self.operacao_var.get() != "Morfismo (interpolacao)":
            return

        if self.matriz_a is None or self.matriz_b is None:
            return

        definicao = self.definicoes["Morfismo (interpolacao)"]
        resultado = definicao.executor(self.matriz_a, self.matriz_b, [self.var_slider_morfismo.get()])
        self._processar_resultado(resultado)

    def _configurar_entrada(self, entrada, texto, habilitada, mostrar=True):
        entrada.configure(state="normal")
        entrada.delete(0, tk.END)
        entrada.insert(0, texto)
        if mostrar:
            entrada.grid()
        else:
            entrada.grid_remove()
        if not habilitada:
            entrada.configure(state="disabled")

    def _configurar_combo(self, combo, opcoes, valor, habilitada):
        combo.configure(values=opcoes)
        combo.set(valor)
        if habilitada:
            combo.configure(state="readonly")
            combo.grid()
        else:
            combo.configure(state="disabled")
            combo.grid_remove()

    def _organizar_paineis_imagem(self, mostrar_b):
        self.painel_a["frame"].grid_forget()
        self.painel_b["frame"].grid_forget()
        self.painel_resultado["frame"].grid_forget()

        self.container_paineis.grid_columnconfigure(0, weight=0)
        self.container_paineis.grid_columnconfigure(1, weight=0)
        self.container_paineis.grid_columnconfigure(2, weight=0)
        self.container_paineis.grid_rowconfigure(0, weight=0)

        if mostrar_b:
            self.painel_a["frame"].grid(row=0, column=0, padx=(0, 4), pady=4, sticky="nw")
            self.painel_b["frame"].grid(row=0, column=1, padx=4, pady=4, sticky="nw")
            self.painel_resultado["frame"].grid(row=0, column=2, padx=(4, 0), pady=4, sticky="nw")
            return

        self.painel_a["frame"].grid(row=0, column=0, padx=(0, 4), pady=4, sticky="nw")
        self.painel_resultado["frame"].grid(row=0, column=1, padx=(4, 0), pady=4, sticky="nw")

    def carregar_imagem_a(self):
        caminho = self._abrir_dialogo_carregamento("Selecionar imagem A")
        if not caminho:
            return

        try:
            self.matriz_a, maximo = ler_pgm(caminho)
        except Exception as erro:
            messagebox.showerror("Erro de leitura", str(erro))
            return

        altura = len(self.matriz_a)
        largura = len(self.matriz_a[0])
        self.info_a.configure(text=f"Imagem A: {largura}x{altura} | max={maximo}")

        self._atualizar_painel(self.painel_a, self.matriz_a, f"Imagem A: {largura}x{altura}")

    def carregar_imagem_b(self):
        caminho = self._abrir_dialogo_carregamento("Selecionar imagem B")
        if not caminho:
            return

        try:
            self.matriz_b, maximo = ler_pgm(caminho)
        except Exception as erro:
            messagebox.showerror("Erro de leitura", str(erro))
            return

        altura = len(self.matriz_b)
        largura = len(self.matriz_b[0])
        self.info_b.configure(text=f"Imagem B: {largura}x{altura} | max={maximo}")

        self._atualizar_painel(self.painel_b, self.matriz_b, f"Imagem B: {largura}x{altura}")

    def _abrir_dialogo_carregamento(self, titulo):
        return filedialog.askopenfilename(
            title=titulo,
            filetypes=[("PGM ASCII", "*.pgm"), ("Todos os arquivos", "*.*")],
        )

    def aplicar_operacao(self):
        nome_operacao = self.operacao_var.get()
        if not nome_operacao:
            messagebox.showwarning("Aviso", "Selecione uma operacao.")
            return

        if self.matriz_a is None:
            messagebox.showwarning("Aviso", "Carregue a imagem A para processar.")
            return

        definicao = self.definicoes[nome_operacao]

        if definicao.requer_segunda and self.matriz_b is None:
            messagebox.showwarning("Aviso", "Esta operacao requer imagem B.")
            return

        try:
            parametros = self._ler_parametros(definicao)
            if nome_operacao == "Morfismo (interpolacao)" and parametros:
                self._sincronizar_slider_morfismo(parametros[0], aplicar=False)
                parametros[0] = self.var_slider_morfismo.get()
            resultado = definicao.executor(self.matriz_a, self.matriz_b, parametros)
            self._processar_resultado(resultado)
        except Exception as erro:
            messagebox.showerror("Erro", str(erro))

    def _ler_parametros(self, definicao):
        valores = []

        for indice, meta in enumerate(definicao.parametros):
            tipo = meta["tipo"]

            if tipo == "select":
                combo = self.combos_parametros[indice]
                valor = combo.get().strip() or str(meta["padrao"])
            else:
                entrada = self.entradas_parametros[indice]
                texto = entrada.get().strip()

                if tipo is float:
                    texto = texto.replace(",", ".")

                if texto == "":
                    texto = str(meta["padrao"])

                try:
                    valor = tipo(texto)
                except ValueError as erro:
                    raise ValueError(f"Parametro invalido: {meta['rotulo']}") from erro

            valores.append(valor)

        nome_operacao = self.operacao_var.get()
        if self._operacao_usa_elemento_estruturante(nome_operacao):
            elemento = self._ler_elemento_estruturante_como_texto()
            if nome_operacao.startswith("Morfologia binaria"):
                if len(valores) >= 2:
                    valores[1] = elemento
                else:
                    valores.append(elemento)
            else:
                if valores:
                    valores[0] = elemento
                else:
                    valores.append(elemento)

        return valores

    def _processar_resultado(self, resultado):
        self.matriz_resultado = resultado["matriz"]

        altura = len(self.matriz_resultado)
        largura = len(self.matriz_resultado[0]) if altura > 0 else 0
        self._atualizar_painel(
            self.painel_resultado,
            self.matriz_resultado,
            f"Resultado: {largura}x{altura}",
        )

        nome_operacao = self.operacao_var.get()
        definicao = self.definicoes.get(nome_operacao)
        if definicao and definicao.requer_segunda:
            self.info_c.configure(text=f"Imagem C (resultado): {largura}x{altura} gerada da operacao entre A e B")
        else:
            self.info_c.configure(text=f"Imagem Resultado: {largura}x{altura}")

        hist_original = resultado.get("hist_original")
        hist_equalizado = resultado.get("hist_equalizado")

        if hist_original is not None:
            self._desenhar_histograma(self.canvas_hist_original, hist_original)
        else:
            self.canvas_hist_original.delete("all")

        if hist_equalizado is not None:
            self._desenhar_histograma(self.canvas_hist_equalizado, hist_equalizado)
        else:
            self.canvas_hist_equalizado.delete("all")

    def _executar_equalizacao_histograma(self, matriz):
        matriz_eq, hist_o, hist_e, _mapa = self.histograma.equalizar_histograma(matriz)
        return {
            "matriz": matriz_eq,
            "hist_original": hist_o,
            "hist_equalizado": hist_e,
        }

    def _executar_morfologia_binaria(self, matriz, parametros, tipo):
        limiar = parametros[0]
        elemento = parametros[1]
        base = self.morfologia_binaria.binarizar(matriz, limiar)

        if tipo == "dilatacao":
            return self.morfologia_binaria.dilatacao_binaria(base, elemento)
        if tipo == "erosao":
            return self.morfologia_binaria.erosao_binaria(base, elemento)
        if tipo == "abertura":
            return self.morfologia_binaria.abertura_binaria(base, elemento)
        if tipo == "fechamento":
            return self.morfologia_binaria.fechamento_binaria(base, elemento)
        if tipo == "gradiente":
            return self.morfologia_binaria.gradiente_binaria(base, elemento)
        if tipo == "contorno_externo":
            return self.morfologia_binaria.contorno_externo_binaria(base, elemento)
        if tipo == "contorno_interno":
            return self.morfologia_binaria.contorno_interno_binaria(base, elemento)

        raise ValueError("Operacao morfologica binaria invalida.")

    def _executar_morfologia_cinza(self, matriz, tipo):

        if tipo == "dilatacao":
            return self.morfologia_cinza.dilatacao_cinza(matriz)
        if tipo == "erosao":
            return self.morfologia_cinza.erosao_cinza(matriz)
        if tipo == "abertura":
            return self.morfologia_cinza.abertura_cinza(matriz)
        if tipo == "fechamento":
            return self.morfologia_cinza.fechamento_cinza(matriz)
        if tipo == "gradiente":
            return self.morfologia_cinza.gradiente_cinza(matriz)
        if tipo == "contorno_externo":
            return self.morfologia_cinza.contorno_externo_cinza(matriz)
        if tipo == "contorno_interno":
            return self.morfologia_cinza.contorno_interno_cinza(matriz)

        raise ValueError("Operacao morfologica em cinza invalida.")

    def _atualizar_painel(self, painel, matriz, descricao):
        painel["matriz"] = matriz
        painel["info"].configure(text=descricao)
        self._mostrar_matriz_texto(painel["texto"], matriz)
        self._desenhar_imagem(painel["canvas"], matriz, painel["chave"])

    def _mostrar_matriz_texto(self, widget_texto, matriz):
        widget_texto.delete("1.0", tk.END)
        widget_texto.tag_remove("pixel_selecionado", "1.0", tk.END)
        if not matriz or not matriz[0]:
            return

        for linha in matriz:
            widget_texto.insert(tk.END, " ".join(f"{valor:3d}" for valor in linha) + "\n")

    def _ao_clicar_pixel_matriz(self, evento, chave_painel):
        painel = self._obter_painel_por_chave(chave_painel)
        if painel is None or not painel.get("matriz"):
            return "break"

        widget = painel["texto"]
        indice = widget.index(f"@{evento.x},{evento.y}")
        linha_txt, coluna_txt = indice.split(".")

        linha = int(linha_txt) - 1
        coluna_char = int(coluna_txt)

        matriz = painel["matriz"]
        if linha < 0 or linha >= len(matriz):
            return "break"

        largura = len(matriz[0])
        coluna = max(0, min(largura - 1, coluna_char // 4))

        self._destacar_pixel_nas_matrizes(linha, coluna)
        return "break"

    def _obter_painel_por_chave(self, chave):
        for painel in [self.painel_a, self.painel_b, self.painel_resultado]:
            if painel.get("chave") == chave:
                return painel
        return None

    def _destacar_pixel_nas_matrizes(self, linha, coluna):
        for painel in [self.painel_a, self.painel_b, self.painel_resultado]:
            widget = painel["texto"]
            widget.tag_remove("pixel_selecionado", "1.0", tk.END)

            matriz = painel.get("matriz")
            if not matriz or not matriz[0]:
                continue

            if linha >= len(matriz) or coluna >= len(matriz[0]):
                continue

            inicio_coluna = coluna * 4
            inicio = f"{linha + 1}.{inicio_coluna}"
            fim = f"{linha + 1}.{inicio_coluna + 3}"
            widget.tag_add("pixel_selecionado", inicio, fim)
            widget.tag_configure("pixel_selecionado", background="#f7d774")

    def _desenhar_imagem(self, canvas, matriz, chave):
        if not matriz or not matriz[0]:
            return

        matriz_visual = self._redimensionar_para_visualizacao(
            matriz,
            self.LARGURA_PAINEL_IMAGEM,
            self.ALTURA_PAINEL_IMAGEM,
        )
        altura = len(matriz_visual)
        largura = len(matriz_visual[0])

        imagem = tk.PhotoImage(width=largura, height=altura)
        for i in range(altura):
            linha_cores = []
            for valor in matriz_visual[i]:
                if valor < 0:
                    valor = 0
                if valor > 255:
                    valor = 255
                linha_cores.append(f"#{valor:02x}{valor:02x}{valor:02x}")
            imagem.put("{" + " ".join(linha_cores) + "}", to=(0, i))

        canvas.delete("all")
        canvas.update_idletasks()
        x_centro = int(canvas.winfo_width()) // 2
        y_centro = int(canvas.winfo_height()) // 2
        canvas.create_image(x_centro, y_centro, image=imagem)

        self.imagens_tk[chave] = imagem

    def _redimensionar_para_visualizacao(self, matriz, largura_limite, altura_limite):
        altura = len(matriz)
        largura = len(matriz[0])

        fator = min(largura_limite / largura, altura_limite / altura)
        if fator >= 1.0:
            return matriz

        nova_largura = max(1, int(round(largura * fator)))
        nova_altura = max(1, int(round(altura * fator)))

        saida = []
        for i in range(nova_altura):
            linha = []
            for j in range(nova_largura):
                origem_i = int(i / fator)
                origem_j = int(j / fator)

                if origem_i >= altura:
                    origem_i = altura - 1
                if origem_j >= largura:
                    origem_j = largura - 1

                linha.append(matriz[origem_i][origem_j])
            saida.append(linha)

        return saida

    def _desenhar_histograma(self, canvas, histograma):
        canvas.delete("all")
        if not histograma:
            return

        canvas.update_idletasks()
        largura = int(canvas.winfo_width())
        altura = int(canvas.winfo_height())

        if largura <= 0 or altura <= 0:
            return

        valor_maximo = max(histograma)
        if valor_maximo == 0:
            return

        margem_esquerda = 56
        margem_direita = 18
        margem_topo = 16
        margem_base = 44

        largura_grafico = largura - margem_esquerda - margem_direita
        altura_grafico = altura - margem_topo - margem_base
        if largura_grafico <= 0 or altura_grafico <= 0:
            return

        x_inicial = margem_esquerda
        y_base = altura - margem_base
        x_final = largura - margem_direita
        y_topo = margem_topo

        canvas.create_line(x_inicial, y_base, x_final, y_base, width=1)
        canvas.create_line(x_inicial, y_base, x_inicial, y_topo, width=1)

        for x_valor in [0, 64, 128, 192, 255]:
            x_pos = x_inicial + (x_valor / 255.0) * largura_grafico
            canvas.create_line(x_pos, y_base, x_pos, y_base + 5)
            canvas.create_text(x_pos, y_base + 18, text=str(x_valor), anchor="n")

        for fracao in [0.0, 0.25, 0.5, 0.75, 1.0]:
            y_pos = y_base - (fracao * altura_grafico)
            valor_y = int(round(valor_maximo * fracao))
            canvas.create_line(x_inicial - 5, y_pos, x_inicial, y_pos)
            canvas.create_text(x_inicial - 8, y_pos, text=str(valor_y), anchor="e")

        canvas.create_text((x_inicial + x_final) / 2, altura - 10, text="Nivel de cinza (x)")
        canvas.create_text(16, (y_topo + y_base) / 2, text="Frequencia (y)", angle=90)

        passo_x = largura_grafico / 256.0
        for i in range(256):
            valor = histograma[i]
            altura_barra = (valor / valor_maximo) * altura_grafico

            x0 = x_inicial + i * passo_x
            x1 = x_inicial + (i + 1) * passo_x
            y0 = y_base - altura_barra
            y1 = y_base

            canvas.create_rectangle(x0, y0, x1, y1, outline="", fill="#1f77b4")


def iniciar_aplicacao():
    raiz = tk.Tk()
    AplicacaoPDI(raiz)
    raiz.mainloop()
