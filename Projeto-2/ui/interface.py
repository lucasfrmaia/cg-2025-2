import tkinter as tk
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
    LARGURA_PAINEL_IMAGEM = 250
    ALTURA_PAINEL_IMAGEM = 250

    LARGURA_PAINEL_MATRIZ = 10
    ALTURA_PAINEL_MATRIZ = 10

    LARGURA_MAXIMA_JANELA = 1920
    ALTURA_MAXIMA_JANELA = 1080

    def __init__(self, janela):
        self.janela = janela
        self.janela.title("Processamento Digital de Imagens")
        self.janela.geometry("1220x760")
        self.janela.minsize(980, 640)
        self.janela.maxsize(self.LARGURA_MAXIMA_JANELA, self.ALTURA_MAXIMA_JANELA)
        self._diretorio_base = Path(__file__).resolve().parent.parent

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

        self.entradas_parametros = []
        self.combos_parametros = []
        self.checkboxes_parametros = []
        self.vars_checkbox_parametros = []
        self.rotulos_parametros = []
        self.var_slider_morfismo = tk.DoubleVar(value=0.5)
        self.frame_slider_morfismo = None
        self.slider_morfismo = None
        self.rotulo_valor_slider_morfismo = None
        self._job_animacao_morfismo = None
        self._valor_morfismo_alvo = 0.5
        self._valor_morfismo_renderizado = 0.5
        self._passo_animacao_morfismo = 0.35
        self._intervalo_animacao_morfismo_ms = 16
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

def iniciar_aplicacao():
    raiz = tk.Tk()
    AplicacaoPDI(raiz)
    raiz.mainloop()
