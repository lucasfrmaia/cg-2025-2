import tkinter as tk
from tkinter import ttk
import tkinter.font as tkfont
from pathlib import Path

from ui.interface_mixins_interacao import InterfaceInteracaoMixin
from ui.interface_mixins_layout import InterfaceLayoutMixin
from ui.interface_mixins_operacoes import InterfaceOperacoesMixin
from ui.interface_mixins_renderizacao import InterfaceRenderizacaoMixin


class AplicacaoPDI(
    InterfaceLayoutMixin,
    InterfaceOperacoesMixin,
    InterfaceInteracaoMixin,
    InterfaceRenderizacaoMixin,
):
    LARGURA_PAINEL_IMAGEM = 300
    ALTURA_PAINEL_IMAGEM = 280

    LARGURA_PAINEL_MATRIZ = 300
    ALTURA_PAINEL_MATRIZ = 170

    LARGURA_MAXIMA_JANELA = 1920
    ALTURA_MAXIMA_JANELA = 1080

    def __init__(self, janela):
        self.janela = janela
        self.janela.title("Processamento Digital de Imagens")
        self.janela.geometry("1260x780")
        self.janela.minsize(920, 620)
        self.janela.maxsize(self.LARGURA_MAXIMA_JANELA, self.ALTURA_MAXIMA_JANELA)
        self._diretorio_base = Path(__file__).resolve().parent.parent

        self.cores_ui = {
            "bg_app": "#eef3f9",
            "bg_card": "#ffffff",
            "bg_canvas": "#f7fafe",
            "bg_matriz": "#f9fbff",
            "texto": "#1f2a37",
            "texto_secundario": "#5a6778",
            "borda": "#c7d3e3",
            "destaque": "#0f4c81",
            "destaque_hover": "#1b5f9a",
            "destaque_suave": "#dbe8f8",
            "alerta": "#ffe08a",
        }

        self._configurar_estilo_visual()

        self._criar_operadores()

        self.matriz_a = None
        self.matriz_b = None
        self.matriz_resultado = None

        self.definicoes = self._criar_definicoes_operacoes()
        self.sessoes = self._criar_sessoes()
        self.imagens_padrao_por_operacao = self._criar_mapeamento_imagens_padrao()

        self.questao_var = tk.StringVar()
        self.subsessao_var = tk.StringVar()
        self.operacao_var = tk.StringVar()
        self._subsessao_por_questao = {}
        self._operacao_por_contexto = {}
        self._parametros_por_operacao_contexto = {}
        self._contexto_ativo = ""

        self.entradas_parametros = []
        self.combos_parametros = []
        self.checkboxes_parametros = []
        self.vars_checkbox_parametros = []
        self.rotulos_parametros = []
        self.var_slider_morfismo = tk.DoubleVar(value=0.5)
        self.frame_slider_morfismo = None
        self.slider_morfismo = None
        self.botao_animacao_morfismo = None
        self.botao_limpar_contexto = None
        self.rotulo_valor_slider_morfismo = None
        self._job_animacao_morfismo = None
        self._valor_morfismo_alvo = 0.5
        self._valor_morfismo_renderizado = 0.5
        self._passo_animacao_morfismo = 0.03
        self._intervalo_animacao_morfismo_ms = 33
        self._animacao_morfismo_ativa = False
        self._direcao_animacao_morfismo = 1
        self._pontos_morfismo = {"A": [], "B": []}
        self._triangulos_morfismo = [
            (0, 1, 4),
            (1, 2, 4),
            (2, 3, 4),
            (3, 0, 4),
        ]
        self._arraste_ponto_morfismo = None
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

    def _configurar_estilo_visual(self):
        self.janela.configure(bg=self.cores_ui["bg_app"])

        fonte_base = tkfont.nametofont("TkDefaultFont")
        fonte_base.configure(family="Segoe UI", size=10)

        fonte_texto = tkfont.nametofont("TkTextFont")
        fonte_texto.configure(family="Consolas", size=10)

        fonte_titulo = tkfont.nametofont("TkHeadingFont")
        fonte_titulo.configure(family="Segoe UI Semibold", size=11)

        style = ttk.Style(self.janela)
        try:
            style.theme_use("clam")
        except tk.TclError:
            pass

        style.configure("App.TFrame", background=self.cores_ui["bg_app"])
        style.configure(
            "Card.TFrame",
            background=self.cores_ui["bg_card"],
            relief="flat",
        )
        style.configure(
            "Card.TLabelframe",
            background=self.cores_ui["bg_card"],
            bordercolor=self.cores_ui["borda"],
            relief="solid",
            borderwidth=1,
            padding=8,
        )
        style.configure(
            "Card.TLabelframe.Label",
            background=self.cores_ui["bg_card"],
            foreground=self.cores_ui["destaque"],
            font=("Segoe UI Semibold", 10),
        )
        style.configure(
            "App.TLabel",
            background=self.cores_ui["bg_card"],
            foreground=self.cores_ui["texto"],
            font=("Segoe UI", 10),
        )
        style.configure(
            "Card.TCheckbutton",
            background=self.cores_ui["bg_card"],
            foreground=self.cores_ui["texto"],
            font=("Segoe UI", 10),
        )
        style.configure(
            "Hint.TLabel",
            background=self.cores_ui["bg_card"],
            foreground=self.cores_ui["texto_secundario"],
            font=("Segoe UI", 9),
        )
        style.configure(
            "App.TButton",
            font=("Segoe UI Semibold", 10),
            padding=(10, 6),
            borderwidth=0,
            foreground="#ffffff",
            background=self.cores_ui["destaque"],
        )
        style.map(
            "App.TButton",
            background=[("active", self.cores_ui["destaque_hover"]), ("disabled", "#98a7bb")],
            foreground=[("disabled", "#eef2f7")],
        )
        style.configure(
            "Secondary.TButton",
            font=("Segoe UI", 9),
            padding=(8, 5),
        )
        style.configure(
            "App.TCombobox",
            padding=(6, 4),
            fieldbackground="#ffffff",
            background="#ffffff",
        )
        style.configure(
            "App.Vertical.TScrollbar",
            arrowsize=12,
            background="#d9e3f0",
        )
        style.configure(
            "App.Horizontal.TScrollbar",
            arrowsize=12,
            background="#d9e3f0",
        )

def iniciar_aplicacao():
    raiz = tk.Tk()
    AplicacaoPDI(raiz)
    raiz.mainloop()
