import tkinter as tk


class InterfaceRenderizacaoMixin:
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
            widget.tag_configure(
                "pixel_selecionado",
                background=self.cores_ui["alerta"],
                foreground="#1f2a37",
            )

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
        canvas_altura = max(1, int(canvas.winfo_height()))
        canvas_largura = max(1, int(canvas.winfo_width()))
        x_origem = max(6, (canvas_largura - largura) // 2)
        y_centro = canvas_altura // 2
        y_origem = max(0, y_centro - (altura // 2))
        canvas.create_image(x_origem, y_centro, image=imagem, anchor="w")

        painel = self._obter_painel_por_chave(chave)
        if painel is not None:
            painel["mapa_exibicao"] = {
                "x_origem": x_origem,
                "y_origem": y_origem,
                "largura_exib": largura,
                "altura_exib": altura,
                "largura_img": len(matriz[0]),
                "altura_img": len(matriz),
                "escala_x": largura / max(1, len(matriz[0])),
                "escala_y": altura / max(1, len(matriz)),
            }

        self._desenhar_overlay_morfismo(canvas, chave)

        self.imagens_tk[chave] = imagem

    def _desenhar_overlay_morfismo(self, canvas, chave):
        if self.operacao_var.get() != "Morfismo (interpolacao)":
            return
        if chave not in {"A", "B"}:
            return

        painel = self._obter_painel_por_chave(chave)
        if painel is None or not painel.get("matriz"):
            return

        mapa = painel.get("mapa_exibicao")
        if not mapa:
            return

        pontos = self._pontos_morfismo.get(chave, [])
        if not pontos:
            return

        triangulos = self._gerar_triangulos_morfismo(len(pontos))
        for a, b, c in triangulos:
            px1, py1 = self._coordenada_imagem_para_canvas(painel, pontos[a][0], pontos[a][1])
            px2, py2 = self._coordenada_imagem_para_canvas(painel, pontos[b][0], pontos[b][1])
            px3, py3 = self._coordenada_imagem_para_canvas(painel, pontos[c][0], pontos[c][1])
            canvas.create_line(px1, py1, px2, py2, fill="#f39c12", width=2)
            canvas.create_line(px2, py2, px3, py3, fill="#f39c12", width=2)
            canvas.create_line(px3, py3, px1, py1, fill="#f39c12", width=2)

        for indice, (x_img, y_img) in enumerate(pontos):
            px, py = self._coordenada_imagem_para_canvas(painel, x_img, y_img)
            raio = self._raio_ponto_morfismo_tela
            canvas.create_oval(
                px - raio,
                py - raio,
                px + raio,
                py + raio,
                fill="#e74c3c",
                outline="#ffffff",
                width=1,
            )
            canvas.create_text(px, py - 11, text=str(indice + 1), fill="#0f4c81", font=("Segoe UI", 8, "bold"))

    def _coordenada_imagem_para_canvas(self, painel, x_img, y_img):
        mapa = painel.get("mapa_exibicao")
        if not mapa:
            return 0, 0

        px = mapa["x_origem"] + float(x_img) * mapa["escala_x"]
        py = mapa["y_origem"] + float(y_img) * mapa["escala_y"]
        return px, py

    def _coordenada_canvas_para_imagem(self, painel, x_canvas, y_canvas):
        mapa = painel.get("mapa_exibicao")
        if not mapa:
            return None

        x_rel = float(x_canvas) - mapa["x_origem"]
        y_rel = float(y_canvas) - mapa["y_origem"]

        if mapa["largura_exib"] <= 0 or mapa["altura_exib"] <= 0:
            return None

        x_rel = max(0.0, min(float(mapa["largura_exib"] - 1), x_rel))
        y_rel = max(0.0, min(float(mapa["altura_exib"] - 1), y_rel))

        x_img = x_rel / max(1e-9, mapa["escala_x"])
        y_img = y_rel / max(1e-9, mapa["escala_y"])

        x_img = max(0.0, min(float(mapa["largura_img"] - 1), x_img))
        y_img = max(0.0, min(float(mapa["altura_img"] - 1), y_img))

        return x_img, y_img

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

        canvas.create_rectangle(
            x_inicial,
            y_topo,
            x_final,
            y_base,
            outline=self.cores_ui["borda"],
            width=1,
        )
        canvas.create_line(x_inicial, y_base, x_final, y_base, width=1, fill=self.cores_ui["texto_secundario"])
        canvas.create_line(x_inicial, y_base, x_inicial, y_topo, width=1, fill=self.cores_ui["texto_secundario"])

        for x_valor in [0, 64, 128, 192, 255]:
            x_pos = x_inicial + (x_valor / 255.0) * largura_grafico
            canvas.create_line(x_pos, y_base, x_pos, y_base + 5, fill=self.cores_ui["texto_secundario"])
            canvas.create_text(
                x_pos,
                y_base + 18,
                text=str(x_valor),
                anchor="n",
                fill=self.cores_ui["texto_secundario"],
                font=("Segoe UI", 8),
            )

        for fracao in [0.0, 0.25, 0.5, 0.75, 1.0]:
            y_pos = y_base - (fracao * altura_grafico)
            valor_y = int(round(valor_maximo * fracao))
            canvas.create_line(x_inicial - 5, y_pos, x_inicial, y_pos, fill=self.cores_ui["texto_secundario"])
            canvas.create_text(
                x_inicial - 8,
                y_pos,
                text=str(valor_y),
                anchor="e",
                fill=self.cores_ui["texto_secundario"],
                font=("Segoe UI", 8),
            )

        canvas.create_text(
            (x_inicial + x_final) / 2,
            altura - 10,
            text="Nivel de cinza (x)",
            fill=self.cores_ui["texto_secundario"],
            font=("Segoe UI", 8),
        )
        canvas.create_text(
            16,
            (y_topo + y_base) / 2,
            text="Frequencia (y)",
            angle=90,
            fill=self.cores_ui["texto_secundario"],
            font=("Segoe UI", 8),
        )

        passo_x = largura_grafico / 256.0
        for i in range(256):
            valor = histograma[i]
            altura_barra = (valor / valor_maximo) * altura_grafico

            x0 = x_inicial + i * passo_x
            x1 = x_inicial + (i + 1) * passo_x
            y0 = y_base - altura_barra
            y1 = y_base

            canvas.create_rectangle(x0, y0, x1, y1, outline="", fill=self.cores_ui["destaque"])
