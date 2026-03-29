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
        x_origem = 6
        y_centro = int(canvas.winfo_height()) // 2
        canvas.create_image(x_origem, y_centro, image=imagem, anchor="w")

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
