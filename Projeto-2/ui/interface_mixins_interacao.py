import tkinter as tk
from tkinter import filedialog, messagebox


class InterfaceInteracaoMixin:
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
        self._aplicar_imagens_padrao_operacao(self.operacao_var.get())

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
        self._aplicar_imagens_padrao_operacao(self.operacao_var.get())

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
            checkbox = self.checkboxes_parametros[indice]
            var_checkbox = self.vars_checkbox_parametros[indice]

            if indice < len(parametros):
                rotulo.configure(text=parametros[indice]["rotulo"])
                meta = parametros[indice]
                if meta["tipo"] == "select":
                    self._configurar_entrada(entrada, "", habilitada=False, mostrar=False)
                    self._configurar_checkbox(checkbox, var_checkbox, False, habilitada=False)
                    self._configurar_combo(combo, meta.get("opcoes", []), meta["padrao"], habilitada=True)
                elif meta["tipo"] == "checkbox":
                    self._configurar_combo(combo, [], "", habilitada=False)
                    self._configurar_entrada(entrada, "", habilitada=False, mostrar=False)
                    self._configurar_checkbox(checkbox, var_checkbox, bool(meta.get("padrao", False)), habilitada=True)
                else:
                    self._configurar_combo(combo, [], "", habilitada=False)
                    self._configurar_checkbox(checkbox, var_checkbox, False, habilitada=False)
                    self._configurar_entrada(entrada, meta["padrao"], habilitada=True)
            else:
                rotulo.configure(text="")
                self._configurar_combo(combo, [], "", habilitada=False)
                self._configurar_checkbox(checkbox, var_checkbox, False, habilitada=False)
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
            self._atualizar_estado_botao_animacao_morfismo()

        self.botao_b.configure(state="normal" if definicao.requer_segunda else "disabled")

        if definicao.requer_segunda:
            self.painel_resultado["frame"].configure(text="Imagem C (resultado da operacao)")
            self.info_c.configure(text="Imagem C (resultado): execute a operacao usando as imagens A e B")
        else:
            self.painel_resultado["frame"].configure(text="Imagem Processada (resultado)")
            self.info_c.configure(text="Imagem de resultado: execute a operacao selecionada")

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
            self._configurar_checkbox(
                self.checkboxes_parametros[indice],
                self.vars_checkbox_parametros[indice],
                False,
                habilitada=False,
            )

    def _operacao_usa_elemento_estruturante(self, nome_operacao):
        return nome_operacao.startswith("Morfologia binaria")

    def _operacao_usa_elemento_cinza_fixo(self, nome_operacao):
        return nome_operacao.startswith("Morfologia cinza")

    def _mostrar_elemento_estruturante(self, mostrar):
        if self.frame_elemento_estruturante is None:
            return

        if mostrar:
            self.frame_elemento_estruturante.grid(row=3, column=0, columnspan=2, sticky="ew")
        else:
            self.frame_elemento_estruturante.grid_remove()

    def _mostrar_elemento_cinza_fixo(self, mostrar):
        if self.frame_elemento_cinza_fixo is None:
            return

        if mostrar:
            self.frame_elemento_cinza_fixo.grid(row=4, column=0, columnspan=2, sticky="ew")
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
        self.info_c.configure(text="Imagem C (resultado): sera gerada apos executar a operacao")

        self.painel_resultado["frame"].configure(text="Imagem C (resultado da operacao)")

        self._limpar_painel(self.painel_a, "")
        self._limpar_painel(self.painel_b, "")
        self._limpar_painel(self.painel_resultado, "")

        self.canvas_hist_original.delete("all")
        self.canvas_hist_equalizado.delete("all")

        self._cancelar_animacao_morfismo()
        self._preencher_elemento_estruturante_padrao()
        self._sincronizar_slider_morfismo(0.5, aplicar=False)

    def _mostrar_slider_morfismo(self, mostrar):
        if self.frame_slider_morfismo is None:
            return

        if mostrar:
            self.frame_slider_morfismo.grid(row=5, column=0, columnspan=2, sticky="ew")
            self._atualizar_estado_botao_animacao_morfismo()
        else:
            self._cancelar_animacao_morfismo()
            self.frame_slider_morfismo.grid_remove()

    def _sincronizar_slider_morfismo(self, valor, aplicar):
        valor = max(0.0, min(1.0, float(valor)))
        self._valor_morfismo_alvo = valor
        self.var_slider_morfismo.set(valor)
        self.rotulo_valor_slider_morfismo.configure(text=f"t = {valor:.2f}")

        if self.entradas_parametros:
            entrada = self.entradas_parametros[0]
            if str(entrada.cget("state")) != "disabled":
                entrada.delete(0, tk.END)
                entrada.insert(0, f"{valor:.2f}")

        if aplicar:
            self._executar_morfismo_em_tempo_real(valor)
        else:
            self._valor_morfismo_renderizado = valor

    def _cancelar_animacao_morfismo(self):
        self._animacao_morfismo_ativa = False
        if self._job_animacao_morfismo is not None:
            self.janela.after_cancel(self._job_animacao_morfismo)
            self._job_animacao_morfismo = None
        self._atualizar_estado_botao_animacao_morfismo()

    def _iniciar_animacao_morfismo(self):
        if self._job_animacao_morfismo is None and self._animacao_morfismo_ativa:
            self._job_animacao_morfismo = self.janela.after(
                self._intervalo_animacao_morfismo_ms,
                self._animar_morfismo_frame,
            )

    def _animar_morfismo_frame(self):
        self._job_animacao_morfismo = None

        if not self._animacao_morfismo_ativa:
            return

        if self.operacao_var.get() != "Morfismo (interpolacao)":
            self._cancelar_animacao_morfismo()
            return

        if self.matriz_a is None or self.matriz_b is None:
            self._cancelar_animacao_morfismo()
            return

        proximo_t = self._valor_morfismo_renderizado + self._passo_animacao_morfismo
        finalizar_animacao = proximo_t >= 1.0
        if finalizar_animacao:
            proximo_t = 1.0

        self._valor_morfismo_renderizado = proximo_t
        self._sincronizar_slider_morfismo(proximo_t, aplicar=False)
        self._executar_morfismo_em_tempo_real(proximo_t)

        if finalizar_animacao:
            self._cancelar_animacao_morfismo()
            return

        self._iniciar_animacao_morfismo()

    def _alternar_animacao_morfismo(self):
        if self.operacao_var.get() != "Morfismo (interpolacao)":
            messagebox.showwarning("Aviso", "Selecione a operacao Morfismo (interpolacao).")
            return

        if self._animacao_morfismo_ativa:
            self._cancelar_animacao_morfismo()
            return

        if self.matriz_a is None or self.matriz_b is None:
            messagebox.showwarning("Aviso", "Carregue as imagens A e B para animar o morfismo.")
            return

        self._animacao_morfismo_ativa = True
        self._direcao_animacao_morfismo = 1
        self._sincronizar_slider_morfismo(0.0, aplicar=False)
        self._executar_morfismo_em_tempo_real(0.0)
        self._atualizar_estado_botao_animacao_morfismo()
        self._iniciar_animacao_morfismo()

    def _atualizar_estado_botao_animacao_morfismo(self):
        if self.botao_animacao_morfismo is None:
            return

        if self._animacao_morfismo_ativa:
            self.botao_animacao_morfismo.configure(text="Parar animacao")
        else:
            self.botao_animacao_morfismo.configure(text="Iniciar animacao")

    def _ao_mover_slider_morfismo(self, valor=None):
        return

    def _executar_morfismo_em_tempo_real(self, valor_t=None):
        if self.operacao_var.get() != "Morfismo (interpolacao)":
            return

        if self.matriz_a is None or self.matriz_b is None:
            return

        if valor_t is None:
            valor_t = self.var_slider_morfismo.get()

        definicao = self.definicoes["Morfismo (interpolacao)"]
        resultado = definicao.executor(self.matriz_a, self.matriz_b, [valor_t])
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

    def _configurar_checkbox(self, checkbox, variavel, valor, habilitada):
        variavel.set(bool(valor))
        if habilitada:
            checkbox.configure(state="normal")
            checkbox.grid()
        else:
            checkbox.configure(state="disabled")
            checkbox.grid_remove()

    def _organizar_paineis_imagem(self, mostrar_b):
        self.painel_a["frame"].grid_forget()
        self.painel_b["frame"].grid_forget()
        self.painel_resultado["frame"].grid_forget()

        self.container_paineis.grid_columnconfigure(0, weight=1)
        self.container_paineis.grid_columnconfigure(1, weight=1)
        self.container_paineis.grid_columnconfigure(2, weight=1)
        self.container_paineis.grid_rowconfigure(0, weight=1)

        if mostrar_b:
            self.painel_a["frame"].grid(row=0, column=0, padx=(0, 4), pady=4, sticky="nsew")
            self.painel_b["frame"].grid(row=0, column=1, padx=4, pady=4, sticky="nsew")
            self.painel_resultado["frame"].grid(row=0, column=2, padx=(4, 0), pady=4, sticky="nsew")
            return

        self.container_paineis.grid_columnconfigure(2, weight=0)
        self.painel_a["frame"].grid(row=0, column=0, padx=(0, 4), pady=4, sticky="nsew")
        self.painel_resultado["frame"].grid(row=0, column=1, padx=(4, 0), pady=4, sticky="nsew")

    def carregar_imagem_a(self):
        caminho = self._abrir_dialogo_carregamento("Selecionar imagem A")
        if not caminho:
            return

        self._carregar_imagem_por_caminho(caminho, "A")

    def carregar_imagem_b(self):
        caminho = self._abrir_dialogo_carregamento("Selecionar imagem B")
        if not caminho:
            return

        self._carregar_imagem_por_caminho(caminho, "B")

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
            elif tipo == "checkbox":
                valor = bool(self.vars_checkbox_parametros[indice].get())
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
            self.info_c.configure(text=f"Imagem C (resultado): {largura}x{altura} gerada a partir da operacao entre A e B")
        else:
            self.info_c.configure(text=f"Imagem de resultado: {largura}x{altura}")

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
