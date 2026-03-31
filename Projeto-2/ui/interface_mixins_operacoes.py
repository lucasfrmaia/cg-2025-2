from pathlib import Path
from tkinter import messagebox

from operacoes.histograma import HistogramaImagem
from operacoes.morfismo import MorfismoImagem
from operacoes.morfologia import MorfologiaBinariaImagem, MorfologiaCinzaImagem
from operacoes.operacoes_aritmeticas import OperacoesAritmeticasImagem
from operacoes.operacoes_logicas import OperacoesLogicasImagem
from operacoes.transformacoes_geometricas import TransformacoesGeometricasImagem
from operacoes.transformacoes_intensidade import TransformacoesIntensidadeImagem
from operacoes.filtros import FiltrosImagem
from utils.leitura_pgm import ler_pgm


class DefinicaoOperacao:
    def __init__(self, nome, executor, parametros=None, requer_segunda=False, exibe_histograma=False):
        self.nome = nome
        self.executor = executor
        self.parametros = parametros or []
        self.requer_segunda = requer_segunda
        self.exibe_histograma = exibe_histograma


class InterfaceOperacoesMixin:
    def _criar_operadores(self):
        self.filtros = FiltrosImagem()
        self.histograma = HistogramaImagem()
        self.morfismo = MorfismoImagem()
        self.morfologia_binaria = MorfologiaBinariaImagem()
        self.morfologia_cinza = MorfologiaCinzaImagem()
        self.aritmetica = OperacoesAritmeticasImagem()
        self.logica = OperacoesLogicasImagem()
        self.geometria = TransformacoesGeometricasImagem()
        self.intensidade = TransformacoesIntensidadeImagem()

    def _criar_mapeamento_imagens_padrao(self):
        mapeamento = {
            "Morfismo (interpolacao)": {
                "a": ["crianca.pgm", "pessoa/crianca.pgm"],
                "b": ["pessoa/jovem.pgm", "jovem.pgm"],
            },
            "Histograma - Equalizar": {"a": ["lena.pgm"]},
        }

        padrao_duas_imagens = {
            "a": ["lena.pgm"],
            "b": ["Airplane.pgm", "airplane.pgm"],
        }

        for nome in [
            "Aritmetica - Soma",
            "Aritmetica - Subtracao",
            "Aritmetica - Multiplicacao",
            "Aritmetica - Divisao",
            "Logica - AND",
            "Logica - OR",
            "Logica - XOR",
        ]:
            mapeamento[nome] = padrao_duas_imagens

        mapeamento['Filtro - Media'] = {"a": ["Lenag.pgm"]}
        mapeamento['Filtro - Mediana'] = {"a": ["lenasalp.pgm"]}

        for nome in [            
            "Filtro - Passa-alta",
            "Filtro - Roberts",
            "Filtro - Roberts cruzado",
            "Filtro - Prewitt",
            "Filtro - Sobel",
            "Filtro - Alto reforco",
        ]:
            mapeamento[nome] = {"a": ["lena.pgm"]}

        for nome in [
            "Intensidade - Negativo",
            "Intensidade - Gamma",
            "Intensidade - Logaritmo",
            "Intensidade - Transferencia geral",
            "Intensidade - Faixa dinamica",
            "Intensidade - Linear",
        ]:
            mapeamento[nome] = {"a": ["lena.pgm"]}

        for nome in [
            "Geometrica - Escala",
            "Geometrica - Translacao",
            "Geometrica - Rotacao",
            "Geometrica - Reflexao",
            "Geometrica - Cisalhamento",
        ]:
            mapeamento[nome] = {"a": ["lena.pgm"]}

        mapeamento["Geometrica - Cisalhamento Arnold"] = {"a": ["gato.pgm", "lena.pgm"]}

        for nome in [
            "Morfologia binaria - Dilatacao",
            "Morfologia binaria - Erosao",
            "Morfologia binaria - Abertura",
            "Morfologia binaria - Fechamento",
            "Morfologia binaria - Gradiente",
            "Morfologia binaria - Contorno externo",
            "Morfologia binaria - Contorno interno",
            "Morfologia binaria - Top-hat",
            "Morfologia binaria - Bottom-hat",
            "Morfologia binaria - Hit-or-miss",
        ]:
            mapeamento[nome] = {"a": ["binarias/imagem_base.pgm", "imagem_base.pgm"]}

        for nome in [
            "Morfologia cinza - Dilatacao",
            "Morfologia cinza - Erosao",
            "Morfologia cinza - Abertura",
            "Morfologia cinza - Fechamento",
            "Morfologia cinza - Gradiente",
            "Morfologia cinza - Contorno externo",
            "Morfologia cinza - Contorno interno",
            "Morfologia cinza - Top-hat",
            "Morfologia cinza - Bottom-hat",
        ]:
            mapeamento[nome] = {"a": ["lena.pgm"]}

        return mapeamento

    def _criar_mapeamento_imagens_padrao_contexto(self):
        return {
            ("Modulo 1 - Filtragem e Operacoes", "Filtragem em imagem unica"): {
                "a": ["lena.pgm"],
            },
            ("Modulo 1 - Filtragem e Operacoes", "Operacoes entre duas imagens"): {
                "a": ["lena.pgm"],
                "b": ["Airplane.pgm", "airplane.pgm"],
            },
            ("Modulo 2 - Morfismo", ""): {
                "a": ["crianca.pgm", "pessoa/crianca.pgm"],
                "b": ["pessoa/jovem.pgm", "jovem.pgm"],
            },
            ("Modulo 3 - Transformacoes de Intensidade", ""): {
                "a": ["lena.pgm"],
            },
            ("Modulo 4 - Histograma", ""): {
                "a": ["lena.pgm"],
            },
            ("Modulo 5 - Morfologia", "Morfologia binaria"): {
                "a": ["binarias/forma_centrada.pgm", "forma_centrada.pgm"],
            },
            ("Modulo 5 - Morfologia", "Morfologia em tons de cinza"): {
                "a": ["lena.pgm"],
            },
            ("Modulo 6 - Transformacoes Geometricas", ""): {
                "a": ["lena.pgm"],
            },
        }

    def _resolver_caminho_imagem_padrao(self, candidatos):
        base_imagens = self._diretorio_base / "imagens"
        for candidato in candidatos:
            caminho = base_imagens / candidato
            if caminho.exists():
                return str(caminho)
        return None

    def _contexto_morfologia_binaria_ativo(self):
        nome_questao = self.questao_var.get() if hasattr(self, "questao_var") else ""
        nome_subsessao = self.subsessao_var.get() if hasattr(self, "subsessao_var") else ""
        nome_operacao = self.operacao_var.get() if hasattr(self, "operacao_var") else ""

        return (
            nome_questao == "Modulo 5 - Morfologia"
            and (
                nome_subsessao == "Morfologia binaria"
                or nome_operacao.startswith("Morfologia binaria")
            )
        )

    def _carregar_imagem_por_caminho(self, caminho, chave):
        try:
            matriz, maximo = ler_pgm(caminho)
        except Exception as erro:
            messagebox.showerror("Erro de leitura", str(erro))
            return False

        nome_arquivo = Path(caminho).name

        if self._contexto_morfologia_binaria_ativo():
            matriz = self.morfologia_binaria.binarizar(matriz)
            maximo = 255

        altura = len(matriz)
        largura = len(matriz[0])

        if chave == "A":
            self.matriz_a = matriz
            if self.operacao_var.get() == "Morfismo (interpolacao)":
                self._inicializar_pontos_morfismo_painel("A", matriz)
            self.info_a.configure(text=f"Imagem A: {nome_arquivo} | {largura}x{altura} | max={maximo}")
            self._atualizar_painel(self.painel_a, matriz, f"Imagem A: {nome_arquivo} | {largura}x{altura}")
            return True

        if chave == "B":
            self.matriz_b = matriz
            if self.operacao_var.get() == "Morfismo (interpolacao)":
                self._inicializar_pontos_morfismo_painel("B", matriz)
            self.info_b.configure(text=f"Imagem B: {nome_arquivo} | {largura}x{altura} | max={maximo}")
            self._atualizar_painel(self.painel_b, matriz, f"Imagem B: {nome_arquivo} | {largura}x{altura}")
            return True

        raise ValueError("Painel de imagem invalido.")

    def _aplicar_imagens_padrao_operacao(self, nome_operacao, apenas_ausentes=True):
        padroes = self.imagens_padrao_por_operacao.get(nome_operacao)
        if not padroes:
            return False

        carregou_alguma = False

        candidatos_a = padroes.get("a", [])
        if candidatos_a and (not apenas_ausentes or self.matriz_a is None):
            caminho_a = self._resolver_caminho_imagem_padrao(candidatos_a)
            if caminho_a:
                carregou_alguma = self._carregar_imagem_por_caminho(caminho_a, "A") or carregou_alguma

        candidatos_b = padroes.get("b", [])
        if candidatos_b and (not apenas_ausentes or self.matriz_b is None):
            caminho_b = self._resolver_caminho_imagem_padrao(candidatos_b)
            if caminho_b:
                carregou_alguma = self._carregar_imagem_por_caminho(caminho_b, "B") or carregou_alguma

        return carregou_alguma

    def _aplicar_imagens_padrao_contexto(self, apenas_ausentes=True):
        contexto = (self.questao_var.get(), self.subsessao_var.get())
        padroes = self.imagens_padrao_por_contexto.get(contexto)

        if padroes is None:
            padroes = self.imagens_padrao_por_contexto.get((self.questao_var.get(), ""))

        if not padroes:
            return False

        carregou_alguma = False
        candidatos_a = padroes.get("a", [])
        candidatos_b = padroes.get("b", [])

        if candidatos_a and (not apenas_ausentes or self.matriz_a is None):
            caminho_a = self._resolver_caminho_imagem_padrao(candidatos_a)
            if caminho_a:
                carregou_alguma = self._carregar_imagem_por_caminho(caminho_a, "A") or carregou_alguma

        if candidatos_b and (not apenas_ausentes or self.matriz_b is None):
            caminho_b = self._resolver_caminho_imagem_padrao(candidatos_b)
            if caminho_b:
                carregou_alguma = self._carregar_imagem_por_caminho(caminho_b, "B") or carregou_alguma

        return carregou_alguma

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
                {"rotulo": "Fator de reforco", "padrao": "1.5", "tipo": float},
            ],
        )

        operacoes["Aritmetica - Soma"] = DefinicaoOperacao(
            "Aritmetica - Soma",
            lambda a, b, p: retorno(self.aritmetica.soma(a, b, p[0])),
            parametros=[
                {"rotulo": "Normalizar resultado (0..255)", "padrao": True, "tipo": "checkbox"},
            ],
            requer_segunda=True,
        )
        operacoes["Aritmetica - Subtracao"] = DefinicaoOperacao(
            "Aritmetica - Subtracao",
            lambda a, b, p: retorno(self.aritmetica.subtracao(a, b, p[0])),
            parametros=[
                {"rotulo": "Normalizar resultado (0..255)", "padrao": True, "tipo": "checkbox"},
            ],
            requer_segunda=True,
        )
        operacoes["Aritmetica - Multiplicacao"] = DefinicaoOperacao(
            "Aritmetica - Multiplicacao",
            lambda a, b, p: retorno(self.aritmetica.multiplicacao(a, b, p[0])),
            parametros=[
                {"rotulo": "Normalizar resultado (0..255)", "padrao": True, "tipo": "checkbox"},
            ],
            requer_segunda=True,
        )
        operacoes["Aritmetica - Divisao"] = DefinicaoOperacao(
            "Aritmetica - Divisao",
            lambda a, b, p: retorno(self.aritmetica.divisao(a, b, p[0])),
            parametros=[
                {"rotulo": "Normalizar resultado (0..255)", "padrao": True, "tipo": "checkbox"},
            ],
            requer_segunda=True,
        )

        operacoes["Logica - AND"] = DefinicaoOperacao(
            "Logica - AND",
            lambda a, b, _p: retorno(self.logica.operacao_and(a, b)),
            requer_segunda=True,
        )
        operacoes["Logica - OR"] = DefinicaoOperacao(
            "Logica - OR",
            lambda a, b, _p: retorno(self.logica.operacao_or(a, b)),
            requer_segunda=True,
        )
        operacoes["Logica - XOR"] = DefinicaoOperacao(
            "Logica - XOR",
            lambda a, b, _p: retorno(self.logica.operacao_xor(a, b)),
            requer_segunda=True,
        )

        operacoes["Morfismo (interpolacao)"] = DefinicaoOperacao(
            "Morfismo (interpolacao)",
            lambda a, b, p: retorno(self.morfismo.interpolar_morfismo(a, b, p[0], p[1] if len(p) > 1 else None)),
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
        operacoes["Intensidade - Transferencia geral"] = DefinicaoOperacao(
            "Intensidade - Transferencia geral",
            lambda a, _b, p: retorno(self.intensidade.funcao_transferencia_geral(a, p[0], p[1])),
            parametros=[
                {"rotulo": "Centro w", "padrao": "128", "tipo": float},
                {"rotulo": "Sigma", "padrao": "20", "tipo": float},
            ],
        )
        operacoes["Intensidade - Faixa dinamica"] = DefinicaoOperacao(
            "Intensidade - Faixa dinamica",
            lambda a, _b, p: retorno(self.intensidade.faixa_dinamica(a, p[0])),
            parametros=[
                {"rotulo": "Target", "padrao": "255", "tipo": float},
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

        opcoes_elemento_binario = ["quadrado 3x3", "quadrado 5x5"]
        opcoes_mascara_hit_or_miss = list(self.morfologia_binaria.MASCARAS_HIT_OR_MISS.keys())
        operacoes["Morfologia binaria - Dilatacao"] = DefinicaoOperacao(
            "Morfologia binaria - Dilatacao",
            lambda a, _b, p: retorno(self._executar_morfologia_binaria(a, p, "dilatacao")),
            parametros=[
                {
                    "rotulo": "Elemento estruturante",
                    "padrao": "quadrado 3x3",
                    "tipo": "select",
                    "opcoes": opcoes_elemento_binario,
                },
            ],
        )
        operacoes["Morfologia binaria - Erosao"] = DefinicaoOperacao(
            "Morfologia binaria - Erosao",
            lambda a, _b, p: retorno(self._executar_morfologia_binaria(a, p, "erosao")),
            parametros=[
                {
                    "rotulo": "Elemento estruturante",
                    "padrao": "quadrado 3x3",
                    "tipo": "select",
                    "opcoes": opcoes_elemento_binario,
                },
            ],
        )
        operacoes["Morfologia binaria - Abertura"] = DefinicaoOperacao(
            "Morfologia binaria - Abertura",
            lambda a, _b, p: retorno(self._executar_morfologia_binaria(a, p, "abertura")),
            parametros=[
                {
                    "rotulo": "Elemento estruturante",
                    "padrao": "quadrado 3x3",
                    "tipo": "select",
                    "opcoes": opcoes_elemento_binario,
                },
            ],
        )
        operacoes["Morfologia binaria - Fechamento"] = DefinicaoOperacao(
            "Morfologia binaria - Fechamento",
            lambda a, _b, p: retorno(self._executar_morfologia_binaria(a, p, "fechamento")),
            parametros=[
                {
                    "rotulo": "Elemento estruturante",
                    "padrao": "quadrado 3x3",
                    "tipo": "select",
                    "opcoes": opcoes_elemento_binario,
                },
            ],
        )
        operacoes["Morfologia binaria - Gradiente"] = DefinicaoOperacao(
            "Morfologia binaria - Gradiente",
            lambda a, _b, p: retorno(self._executar_morfologia_binaria(a, p, "gradiente")),
            parametros=[
                {
                    "rotulo": "Elemento estruturante",
                    "padrao": "quadrado 3x3",
                    "tipo": "select",
                    "opcoes": opcoes_elemento_binario,
                },
            ],
        )
        operacoes["Morfologia binaria - Contorno externo"] = DefinicaoOperacao(
            "Morfologia binaria - Contorno externo",
            lambda a, _b, p: retorno(self._executar_morfologia_binaria(a, p, "contorno_externo")),
            parametros=[
                {
                    "rotulo": "Elemento estruturante",
                    "padrao": "quadrado 3x3",
                    "tipo": "select",
                    "opcoes": opcoes_elemento_binario,
                },
            ],
        )
        operacoes["Morfologia binaria - Contorno interno"] = DefinicaoOperacao(
            "Morfologia binaria - Contorno interno",
            lambda a, _b, p: retorno(self._executar_morfologia_binaria(a, p, "contorno_interno")),
            parametros=[
                {
                    "rotulo": "Elemento estruturante",
                    "padrao": "quadrado 3x3",
                    "tipo": "select",
                    "opcoes": opcoes_elemento_binario,
                },
            ],
        )
        operacoes["Morfologia binaria - Top-hat"] = DefinicaoOperacao(
            "Morfologia binaria - Top-hat",
            lambda a, _b, p: retorno(self._executar_morfologia_binaria(a, p, "top_hat")),
            parametros=[
                {
                    "rotulo": "Elemento estruturante",
                    "padrao": "quadrado 3x3",
                    "tipo": "select",
                    "opcoes": opcoes_elemento_binario,
                },
            ],
        )
        operacoes["Morfologia binaria - Bottom-hat"] = DefinicaoOperacao(
            "Morfologia binaria - Bottom-hat",
            lambda a, _b, p: retorno(self._executar_morfologia_binaria(a, p, "bottom_hat")),
            parametros=[
                {
                    "rotulo": "Elemento estruturante",
                    "padrao": "quadrado 3x3",
                    "tipo": "select",
                    "opcoes": opcoes_elemento_binario,
                },
            ],
        )
        operacoes["Morfologia binaria - Hit-or-miss"] = DefinicaoOperacao(
            "Morfologia binaria - Hit-or-miss",
            lambda a, _b, p: retorno(self._executar_morfologia_binaria(a, p, "hit_or_miss")),
            parametros=[
                {
                    "rotulo": "Mascara",
                    "padrao": opcoes_mascara_hit_or_miss[0],
                    "tipo": "select",
                    "opcoes": opcoes_mascara_hit_or_miss,
                },
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
        operacoes["Morfologia cinza - Top-hat"] = DefinicaoOperacao(
            "Morfologia cinza - Top-hat",
            lambda a, _b, _p: retorno(self._executar_morfologia_cinza(a, "top_hat")),
        )
        operacoes["Morfologia cinza - Bottom-hat"] = DefinicaoOperacao(
            "Morfologia cinza - Bottom-hat",
            lambda a, _b, _p: retorno(self._executar_morfologia_cinza(a, "bottom_hat")),
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
                    "opcoes": ["horizontal", "vertical"],
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
        operacoes["Geometrica - Cisalhamento Arnold"] = DefinicaoOperacao(
            "Geometrica - Cisalhamento Arnold",
            lambda a, _b, p: retorno(self.geometria.cisalhamento_arnold(a, p[0])),
            parametros=[
                {"rotulo": "Iteracoes (X)", "padrao": "1", "tipo": int},
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
                "descricao": "Ajustes de contraste e brilho com funcoes ponto a ponto nos niveis de cinza.",
                "operacoes": [
                    "Intensidade - Negativo",
                    "Intensidade - Gamma",
                    "Intensidade - Logaritmo",
                    "Intensidade - Transferencia geral",
                    "Intensidade - Faixa dinamica",
                    "Intensidade - Linear",
                ],
            },
            "Modulo 4 - Histograma": {
                "descricao": "Analise da distribuicao tonal e equalizacao para melhoria de contraste.",
                "operacoes": ["Histograma - Equalizar"],
            },
            "Modulo 5 - Morfologia": {
                "descricao": "Operacoes morfologicas para imagem binaria e em tons de cinza com elemento estruturante personalizado (ate 3x3, +1 define a origem).",
                "sub_sessoes": {
                    "Morfologia binaria": [
                        "Morfologia binaria - Dilatacao",
                        "Morfologia binaria - Erosao",
                        "Morfologia binaria - Abertura",
                        "Morfologia binaria - Fechamento",
                        "Morfologia binaria - Gradiente",
                        "Morfologia binaria - Contorno externo",
                        "Morfologia binaria - Contorno interno",
                        "Morfologia binaria - Top-hat",
                        "Morfologia binaria - Bottom-hat",
                        "Morfologia binaria - Hit-or-miss",
                    ],
                    "Morfologia em tons de cinza": [
                        "Morfologia cinza - Dilatacao",
                        "Morfologia cinza - Erosao",
                        "Morfologia cinza - Abertura",
                        "Morfologia cinza - Fechamento",
                        "Morfologia cinza - Gradiente",
                        "Morfologia cinza - Contorno externo",
                        "Morfologia cinza - Contorno interno",
                        "Morfologia cinza - Top-hat",
                        "Morfologia cinza - Bottom-hat",
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
                    "Geometrica - Cisalhamento Arnold",
                ],
            },
        }

    def _executar_equalizacao_histograma(self, matriz):
        matriz_eq, hist_o, hist_e, _mapa = self.histograma.equalizar_histograma(matriz)
        return {
            "matriz": matriz_eq,
            "hist_original": hist_o,
            "hist_equalizado": hist_e,
        }

    def _executar_morfologia_binaria(self, matriz, parametros, tipo):
        elemento = parametros[0] if parametros else "quadrado 3x3"

        if tipo == "dilatacao":
            return self.morfologia_binaria.dilatacao_binaria(matriz, elemento)
        if tipo == "erosao":
            return self.morfologia_binaria.erosao_binaria(matriz, elemento)
        if tipo == "abertura":
            return self.morfologia_binaria.abertura_binaria(matriz, elemento)
        if tipo == "fechamento":
            return self.morfologia_binaria.fechamento_binaria(matriz, elemento)
        if tipo == "gradiente":
            return self.morfologia_binaria.gradiente_binaria(matriz, elemento)
        if tipo == "contorno_externo":
            return self.morfologia_binaria.contorno_externo_binaria(matriz, elemento)
        if tipo == "contorno_interno":
            return self.morfologia_binaria.contorno_interno_binaria(matriz, elemento)
        if tipo == "top_hat":
            return self.morfologia_binaria.top_hat_binaria(matriz, elemento)
        if tipo == "bottom_hat":
            return self.morfologia_binaria.bottom_hat_binaria(matriz, elemento)
        if tipo == "hit_or_miss":
            return self.morfologia_binaria.hit_or_miss_binaria(matriz, elemento)

        raise ValueError("Operacao morfologica binaria invalida.")

    def _executar_morfologia_cinza(self, matriz, tipo):
        elemento = self._ler_elemento_estruturante_cinza_como_texto()

        if tipo == "dilatacao":
            return self.morfologia_cinza.dilatacao_cinza(matriz, elemento)
        if tipo == "erosao":
            return self.morfologia_cinza.erosao_cinza(matriz, elemento)
        if tipo == "abertura":
            return self.morfologia_cinza.abertura_cinza(matriz, elemento)
        if tipo == "fechamento":
            return self.morfologia_cinza.fechamento_cinza(matriz, elemento)
        if tipo == "gradiente":
            return self.morfologia_cinza.gradiente_cinza(matriz, elemento)
        if tipo == "contorno_externo":
            return self.morfologia_cinza.contorno_externo_cinza(matriz, elemento)
        if tipo == "contorno_interno":
            return self.morfologia_cinza.contorno_interno_cinza(matriz, elemento)
        if tipo == "top_hat":
            return self.morfologia_cinza.top_hat_cinza(matriz, elemento)
        if tipo == "bottom_hat":
            return self.morfologia_cinza.bottom_hat_cinza(matriz, elemento)

        raise ValueError("Operacao morfologica em tons de cinza invalida.")
