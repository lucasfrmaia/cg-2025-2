import tkinter as tk
import time
from tkinter import filedialog, messagebox


class InterfaceInteracaoMixin:
    def _morfismo_ativo(self):
        return self.operacao_var.get() == "Morfismo (interpolacao)"

    def _resetar_pontos_morfismo(self):
        self._pontos_morfismo = {"A": [], "B": []}
        self._arraste_ponto_morfismo = None

    def _inicializar_pontos_morfismo_painel(self, chave_painel, matriz, preservar=False):
        if chave_painel not in {"A", "B"}:
            return

        if not matriz or not matriz[0]:
            return

        if not preservar:
            self._pontos_morfismo[chave_painel] = []
            return

        altura = float(len(matriz))
        largura = float(len(matriz[0]))

        pontos_ajustados = []
        for x, y in self._pontos_morfismo.get(chave_painel, []):
            x = max(0.0, min(largura - 1.0, float(x)))
            y = max(0.0, min(altura - 1.0, float(y)))
            pontos_ajustados.append([x, y])

        self._pontos_morfismo[chave_painel] = pontos_ajustados

    def _estado_pontos_morfismo_valido(self):
        pontos_a = self._pontos_morfismo.get("A", [])
        pontos_b = self._pontos_morfismo.get("B", [])
        return len(pontos_a) >= 3 and len(pontos_a) == len(pontos_b)

    def _gerar_triangulos_morfismo(self, total_pontos):
        if total_pontos < 3:
            return []
        return [(0, indice, indice + 1) for indice in range(1, total_pontos - 1)]

    def _limpar_pontos_morfismo(self):
        self._cancelar_animacao_morfismo()
        self._resetar_pontos_morfismo()

        if self.matriz_a is not None:
            self._desenhar_imagem(self.painel_a["canvas"], self.matriz_a, "A")
        if self.matriz_b is not None:
            self._desenhar_imagem(self.painel_b["canvas"], self.matriz_b, "B")

        self.matriz_resultado = None
        self._limpar_painel(self.painel_resultado, "")
        self.info_c.configure(text="Imagem C (resultado): sera gerada apos executar a operacao")

    def _indice_ponto_morfismo_proximo(self, chave_painel, painel, x_img, y_img):
        pontos = self._pontos_morfismo.get(chave_painel, [])
        if not pontos:
            return None

        melhor_indice = None
        melhor_distancia = float("inf")

        for indice, (px, py) in enumerate(pontos):
            cx, cy = self._coordenada_imagem_para_canvas(painel, px, py)
            ex, ey = self._coordenada_imagem_para_canvas(painel, x_img, y_img)
            distancia = ((cx - ex) ** 2 + (cy - ey) ** 2) ** 0.5

            if distancia < melhor_distancia:
                melhor_distancia = distancia
                melhor_indice = indice

        if melhor_distancia <= self._raio_ponto_morfismo_tela + 4:
            return melhor_indice
        return None

    def _obter_triangulos_morfismo_backend(self):
        if not self._morfismo_ativo():
            return None

        if self.matriz_a is None or self.matriz_b is None:
            return None

        if not self._estado_pontos_morfismo_valido():
            return None

        total_pontos = len(self._pontos_morfismo["A"])
        triangulos = self._gerar_triangulos_morfismo(total_pontos)
        if not triangulos:
            return None

        return {
            "vertices_a": [tuple(ponto) for ponto in self._pontos_morfismo["A"]],
            "vertices_b": [tuple(ponto) for ponto in self._pontos_morfismo["B"]],
            "triangulos": triangulos,
        }

    def _ao_press_canvas_morfismo(self, evento, chave_painel):
        if not self._morfismo_ativo():
            return
        if chave_painel not in {"A", "B"}:
            return

        painel = self._obter_painel_por_chave(chave_painel)
        if painel is None or not painel.get("matriz"):
            return

        matriz = painel["matriz"]
        self._inicializar_pontos_morfismo_painel(chave_painel, matriz, preservar=True)

        coords = self._coordenada_canvas_para_imagem(painel, evento.x, evento.y)
        if coords is None:
            return

        x_img, y_img = coords
        indice_mais_proximo = self._indice_ponto_morfismo_proximo(
            chave_painel,
            painel,
            x_img,
            y_img,
        )

        if indice_mais_proximo is None:
            self._pontos_morfismo[chave_painel].append([x_img, y_img])
            indice_mais_proximo = len(self._pontos_morfismo[chave_painel]) - 1

        self._arraste_ponto_morfismo = {
            "chave": chave_painel,
            "indice": indice_mais_proximo,
        }
        self._atualizar_ponto_morfismo_arraste(chave_painel, indice_mais_proximo, x_img, y_img)

    def _ao_arrastar_canvas_morfismo(self, evento, chave_painel):
        if not self._morfismo_ativo():
            return

        estado = self._arraste_ponto_morfismo
        if not estado or estado.get("chave") != chave_painel:
            return

        painel = self._obter_painel_por_chave(chave_painel)
        if painel is None or not painel.get("matriz"):
            return

        coords = self._coordenada_canvas_para_imagem(painel, evento.x, evento.y)
        if coords is None:
            return

        x_img, y_img = coords
        self._atualizar_ponto_morfismo_arraste(chave_painel, estado["indice"], x_img, y_img)

    def _ao_soltar_canvas_morfismo(self, _evento, _chave_painel):
        self._arraste_ponto_morfismo = None

    def _atualizar_ponto_morfismo_arraste(self, chave_painel, indice, x_img, y_img):
        painel = self._obter_painel_por_chave(chave_painel)
        if painel is None or not painel.get("matriz"):
            return

        matriz = painel["matriz"]
        largura = len(matriz[0])
        altura = len(matriz)

        x_img = max(0.0, min(float(largura - 1), float(x_img)))
        y_img = max(0.0, min(float(altura - 1), float(y_img)))

        self._inicializar_pontos_morfismo_painel(chave_painel, matriz, preservar=True)

        if indice < 0 or indice >= len(self._pontos_morfismo[chave_painel]):
            return

        self._pontos_morfismo[chave_painel][indice] = [x_img, y_img]
        self._desenhar_imagem(painel["canvas"], matriz, chave_painel)

        if self._estado_pontos_morfismo_valido():
            self._executar_morfismo_em_tempo_real(self._valor_morfismo_renderizado)

    def _obter_chave_contexto(self, nome_questao=None, nome_subsessao=None):
        questao = nome_questao if nome_questao is not None else self.questao_var.get()
        subsessao = nome_subsessao if nome_subsessao is not None else self.subsessao_var.get()
        if not questao:
            return ""
        return f"{questao}::{subsessao or ''}"

    def _salvar_parametros_operacao_atual(self, contexto=None, nome_operacao=None):
        contexto = contexto if contexto is not None else (self._contexto_ativo or self._obter_chave_contexto())
        nome_operacao = nome_operacao if nome_operacao is not None else self.operacao_var.get()

        if not contexto or not nome_operacao:
            return

        definicao = self.definicoes.get(nome_operacao)
        if definicao is None:
            return

        valores = []
        for indice, meta in enumerate(definicao.parametros):
            tipo = meta["tipo"]
            if tipo == "select":
                valores.append(self.combos_parametros[indice].get().strip())
            elif tipo == "checkbox":
                valores.append(bool(self.vars_checkbox_parametros[indice].get()))
            else:
                valores.append(self.entradas_parametros[indice].get())

        self._parametros_por_operacao_contexto[(contexto, nome_operacao)] = valores

    def _restaurar_parametros_operacao(self, contexto=None, nome_operacao=None):
        contexto = contexto if contexto is not None else self._obter_chave_contexto()
        nome_operacao = nome_operacao if nome_operacao is not None else self.operacao_var.get()

        valores = self._parametros_por_operacao_contexto.get((contexto, nome_operacao))
        if not valores:
            return

        definicao = self.definicoes.get(nome_operacao)
        if definicao is None:
            return

        for indice, meta in enumerate(definicao.parametros):
            if indice >= len(valores):
                break

            valor = valores[indice]
            tipo = meta["tipo"]
            if tipo == "select":
                combo = self.combos_parametros[indice]
                opcoes = [str(opcao) for opcao in combo.cget("values")]
                valor_texto = str(valor)
                if valor_texto in opcoes:
                    combo.set(valor_texto)
            elif tipo == "checkbox":
                self.vars_checkbox_parametros[indice].set(bool(valor))
            else:
                entrada = self.entradas_parametros[indice]
                if str(entrada.cget("state")) != "disabled":
                    entrada.delete(0, tk.END)
                    entrada.insert(0, str(valor))

    def _salvar_estado_contexto_ativo(self):
        contexto = self._contexto_ativo
        if not contexto:
            return

        nome_operacao = self.operacao_var.get()
        if nome_operacao:
            self._operacao_por_contexto[contexto] = nome_operacao
            self._salvar_parametros_operacao_atual(contexto=contexto, nome_operacao=nome_operacao)

    def _inicializar_estado(self):
        questoes = list(self.sessoes.keys())
        self.combo_questao.configure(values=questoes)
        if questoes:
            self.questao_var.set(questoes[0])

        self._atualizar_sessao_ativa(forcar_imagens_padrao=True)

    def _ao_mudar_questao(self, _evento=None):
        self._salvar_estado_contexto_ativo()
        self._atualizar_sessao_ativa(forcar_imagens_padrao=True)

    def _ao_mudar_subsessao(self, _evento=None):
        self._salvar_estado_contexto_ativo()
        self._subsessao_por_questao[self.questao_var.get()] = self.subsessao_var.get()

        contexto_destino = self._obter_chave_contexto()
        self._operacao_por_contexto.pop(contexto_destino, None)
        chaves_parametros = [
            chave
            for chave in self._parametros_por_operacao_contexto
            if chave[0] == contexto_destino
        ]
        for chave in chaves_parametros:
            self._parametros_por_operacao_contexto.pop(chave, None)

        self._resetar_estado_inicial_tela()
        self._atualizar_operacoes_sessao(
            forcar_operacao_inicial=True,
            forcar_imagens_padrao=True,
        )

    def _ao_mudar_operacao(self, _evento=None):
        contexto = self._obter_chave_contexto()
        operacao_anterior = self._operacao_por_contexto.get(contexto, "")
        if operacao_anterior:
            self._salvar_parametros_operacao_atual(contexto=contexto, nome_operacao=operacao_anterior)
        if contexto:
            self._operacao_por_contexto[contexto] = self.operacao_var.get()
        self._atualizar_parametros_operacao()
        self._restaurar_parametros_operacao(contexto=contexto, nome_operacao=self.operacao_var.get())
        self._aplicar_imagens_padrao_operacao(self.operacao_var.get(), apenas_ausentes=True)

        if self._morfismo_ativo():
            if self.matriz_a is not None:
                self._inicializar_pontos_morfismo_painel("A", self.matriz_a, preservar=True)
                self._desenhar_imagem(self.painel_a["canvas"], self.matriz_a, "A")
            if self.matriz_b is not None:
                self._inicializar_pontos_morfismo_painel("B", self.matriz_b, preservar=True)
                self._desenhar_imagem(self.painel_b["canvas"], self.matriz_b, "B")
        else:
            self._arraste_ponto_morfismo = None

    def _atualizar_sessao_ativa(self, forcar_imagens_padrao=False):
        nome_questao = self.questao_var.get()
        dados = self.sessoes[nome_questao]

        self.rotulo_descricao_sessao.configure(text=dados["descricao"])

        if "sub_sessoes" in dados:
            nomes_sub = list(dados["sub_sessoes"].keys())
            self.combo_subsessao.configure(values=nomes_sub, state="readonly")
            subsessao_salva = self._subsessao_por_questao.get(nome_questao, "")
            if subsessao_salva in nomes_sub:
                self.subsessao_var.set(subsessao_salva)
            else:
                self.subsessao_var.set(nomes_sub[0])
            self._subsessao_por_questao[nome_questao] = self.subsessao_var.get()
            self.rotulo_subsessao.grid()
            self.combo_subsessao.grid()
        else:
            self.subsessao_var.set("")
            self.combo_subsessao.set("")
            self.combo_subsessao.configure(values=[], state="disabled")
            self.rotulo_subsessao.grid_remove()
            self.combo_subsessao.grid_remove()

        self._atualizar_operacoes_sessao(forcar_imagens_padrao=forcar_imagens_padrao)

    def _obter_operacoes_sessao_atual(self):
        dados = self.sessoes[self.questao_var.get()]

        if "sub_sessoes" in dados:
            return dados["sub_sessoes"][self.subsessao_var.get()]

        return dados["operacoes"]

    def _atualizar_operacoes_sessao(self, forcar_operacao_inicial=False, forcar_imagens_padrao=False):
        operacoes = self._obter_operacoes_sessao_atual()
        self.combo_operacao.configure(values=operacoes)

        contexto = self._obter_chave_contexto()
        self._contexto_ativo = contexto

        if operacoes:
            if forcar_operacao_inicial:
                operacao = operacoes[0]
            else:
                operacao = self._operacao_por_contexto.get(contexto, operacoes[0])
                if operacao not in operacoes:
                    operacao = operacoes[0]

            self.operacao_var.set(operacao)
            self._operacao_por_contexto[contexto] = operacao
        else:
            self.operacao_var.set("")

        self._atualizar_parametros_operacao()
        self._restaurar_parametros_operacao(contexto=contexto, nome_operacao=self.operacao_var.get())
        aplicou_por_operacao = self._aplicar_imagens_padrao_operacao(
            self.operacao_var.get(),
            apenas_ausentes=not (forcar_operacao_inicial or forcar_imagens_padrao),
        )

        if (forcar_operacao_inicial or forcar_imagens_padrao) and not aplicou_por_operacao:
            self._aplicar_imagens_padrao_contexto(apenas_ausentes=False)

    def _limpar_contexto_atual(self):
        contexto = self._obter_chave_contexto()
        if not contexto:
            return

        self._operacao_por_contexto.pop(contexto, None)

        chaves_parametros = [
            chave
            for chave in self._parametros_por_operacao_contexto
            if chave[0] == contexto
        ]
        for chave in chaves_parametros:
            self._parametros_por_operacao_contexto.pop(chave, None)

        self._resetar_estado_inicial_tela()
        self._atualizar_operacoes_sessao(forcar_operacao_inicial=True)

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
        return False

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

    def _preencher_elemento_cinza_padrao(self):
        if self.texto_elemento_cinza_fixo is None:
            return

        self.texto_elemento_cinza_fixo.delete("1.0", tk.END)
        self.texto_elemento_cinza_fixo.insert(
            "1.0",
            self.morfologia_cinza.obter_texto_elemento_estruturante(),
        )

    def _ler_elemento_estruturante_cinza_como_texto(self):
        if self.texto_elemento_cinza_fixo is None:
            return self.morfologia_cinza.obter_texto_elemento_estruturante()

        texto = self.texto_elemento_cinza_fixo.get("1.0", tk.END).strip()
        if not texto:
            return self.morfologia_cinza.obter_texto_elemento_estruturante()
        return texto

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
        self._resetar_pontos_morfismo()
        self._preencher_elemento_estruturante_padrao()
        self._preencher_elemento_cinza_padrao()
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
        self._inicio_animacao_morfismo_ts = None
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

    def _suavizar_progresso_morfismo(self, progresso):
        progresso = max(0.0, min(1.0, float(progresso)))
        return progresso * progresso * (3.0 - 2.0 * progresso)

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

        if self._inicio_animacao_morfismo_ts is None:
            self._inicio_animacao_morfismo_ts = time.perf_counter()

        tempo_decorrido_ms = (time.perf_counter() - self._inicio_animacao_morfismo_ts) * 1000.0
        progresso = tempo_decorrido_ms / max(1.0, float(self._duracao_animacao_morfismo_ms))
        finalizar_animacao = progresso >= 1.0
        proximo_t = self._suavizar_progresso_morfismo(min(1.0, progresso))

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
        self._inicio_animacao_morfismo_ts = time.perf_counter()
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
        resultado = definicao.executor(
            self.matriz_a,
            self.matriz_b,
            [valor_t, self._obter_triangulos_morfismo_backend()],
        )
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

        if self._morfismo_ativo():
            valores.append(self._obter_triangulos_morfismo_backend())

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
