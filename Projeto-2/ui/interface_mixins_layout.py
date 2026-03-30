import tkinter as tk
from tkinter import ttk


class InterfaceLayoutMixin:
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
        self.canvas_principal.bind_all("<MouseWheel>", self._ao_rolar_mouse_vertical)
        self.canvas_principal.bind_all("<Shift-MouseWheel>", self._ao_rolar_mouse_horizontal)

    def _ao_configurar_frame_rolavel(self, _evento=None):
        self.canvas_principal.configure(scrollregion=self.canvas_principal.bbox("all"))

    def _ao_configurar_canvas_rolavel(self, _evento=None):
        self.canvas_principal.configure(scrollregion=self.canvas_principal.bbox("all"))

    def _ao_rolar_mouse_vertical(self, evento):
        if evento.delta:
            deslocamento = int(-evento.delta / 120)
            self.canvas_principal.yview_scroll(deslocamento, "units")

    def _ao_rolar_mouse_horizontal(self, evento):
        if evento.delta:
            deslocamento = int(-evento.delta / 120)
            self.canvas_principal.xview_scroll(deslocamento, "units")

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

        self.info_c = ttk.Label(quadro, text="Imagem C (resultado): sera gerada apos executar a operacao")
        self.info_c.grid(row=2, column=0, columnspan=2, padx=6, pady=(2, 6), sticky="w")

    def _montar_parametros(self):
        quadro = ttk.LabelFrame(self.frame_principal, text="Parametros da Operacao Selecionada")
        quadro.pack(fill="x", padx=10, pady=6)

        for indice in range(3):
            coluna_base = indice * 2
            rotulo = ttk.Label(quadro, text=f"Parametro {indice + 1}")
            entrada = ttk.Entry(quadro, width=18)
            combo = ttk.Combobox(quadro, state="readonly", width=18)
            var_checkbox = tk.BooleanVar(value=False)
            checkbox = ttk.Checkbutton(quadro, variable=var_checkbox)

            rotulo.grid(row=0, column=coluna_base, padx=6, pady=6, sticky="w")
            entrada.grid(row=0, column=coluna_base + 1, padx=6, pady=6, sticky="w")
            combo.grid(row=0, column=coluna_base + 1, padx=6, pady=6, sticky="w")
            checkbox.grid(row=0, column=coluna_base + 1, padx=6, pady=6, sticky="w")
            combo.grid_remove()
            checkbox.grid_remove()

            self.rotulos_parametros.append(rotulo)
            self.entradas_parametros.append(entrada)
            self.combos_parametros.append(combo)
            self.checkboxes_parametros.append(checkbox)
            self.vars_checkbox_parametros.append(var_checkbox)

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
