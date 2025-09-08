import tkinter as tk
#import tk

#print(tk.TkVersion)
#NDCX
def calcular_ndcx(X, Xm, XM, coordenadaInicialX, coordenadaFinalX):
    NDCX = ((X - Xm) / (XM - Xm)) * (coordenadaFinalX - coordenadaInicialX) + coordenadaInicialX
    return NDCX
#NDCY
def calcular_ndcy(Y, Ym, YM, coordenadaInicialY, coordenadaFinalY):
    NDCY = ((Y - Ym) / (YM - Ym)) * (coordenadaFinalY - coordenadaInicialY) + coordenadaInicialY
    return NDCY
#CW
def inp_to_ndc(X, Xm, XM, Y, Ym, YM, coordenadaInicialX, coordenadaFinalX, coordenadaInicialY, coordenadaFinalY):
    NDCX = calcular_ndcx(X, Xm, XM, coordenadaInicialX, coordenadaFinalX)
    NDCY = calcular_ndcy(Y, Ym, YM, coordenadaInicialY, coordenadaFinalY)
    return [NDCX, NDCY]
#DCX
def calcular_dcx(NDCX, NDH, coordenadaInicialX, coordenadaFinalX):
    DCX = round(((NDCX - coordenadaInicialX) * (NDH - 1)) / (coordenadaFinalX - coordenadaInicialX))
    return DCX

def calcular_dcy(NDCY, NDV, coordenadaInicialY, coordenadaFinalY):
    DCY = round(((NDCY - coordenadaInicialY) * (NDV - 1)) / (coordenadaFinalY - coordenadaInicialY))
    return DCY
#DCX e DCY
def ndc_to_dc(NDCX, NDCY, NDH, NDV, coordenadaInicialX, coordenadaFinalX, coordenadaInicialY, coordenadaFinalY):
    DCX = calcular_dcx(NDCX, NDH, coordenadaInicialX, coordenadaFinalX)
    DCY = calcular_dcy(NDCY, NDV, coordenadaInicialY, coordenadaFinalY)
    return [DCX, DCY]
# ndcx, ndcy
def user_to_ndc(X, Xm, XM, Y, Ym, YM):
    ndcx = (X - Xm) / (XM - Xm)
    ndcy = (Y - Ym) / (YM - Ym)
    return ndcx, ndcy

# X e Y
def ndc_to_user(ndcx, Xm, XM, ndcy, Ym, YM):
    X =  Xm + ndcx* (XM - Xm)
    Y = Ym + ndcy * (YM - Ym)
    return X, Y

print("Sistema de Transformacoes de Coordenadas\n")
X = float(input("Digite o valor de X: "))
Y = float(input("Digite o valor de Y: "))

Xm = float(input("Digite o valor de Xm: "))
XM = float(input("Digite o valor de XM: "))
Ym = float(input("Digite o valor de Ym: "))
YM = float(input("Digite o valor de YM: "))

coordenadaInicialX = 0
coordenadaFinalX = 1
coordenadaInicialY = 0
coordenadaFinalY = 1

NDH = float(input("Digite a largura da tela: "))
NDV = float(input("Digite a altura da tela: "))

NDCX = inp_to_ndc(X, Xm, XM, Y, Ym, YM, coordenadaInicialX, coordenadaFinalX, coordenadaInicialY, coordenadaFinalY)[0]
NDCY = inp_to_ndc(X, Xm, XM, Y, Ym, YM, coordenadaInicialX, coordenadaFinalX, coordenadaInicialY, coordenadaFinalY)[1]

DCX = ndc_to_dc(NDCX, NDCY, NDH, NDV, coordenadaInicialX, coordenadaFinalX, coordenadaInicialY, coordenadaFinalY)[0]
DCY = ndc_to_dc(NDCX, NDCY, NDH, NDV, coordenadaInicialX, coordenadaFinalX, coordenadaInicialY, coordenadaFinalY)[1]

user_to_ndc = user_to_ndc(X, Xm, XM, Y, Ym, YM)
ndc_to_user = ndc_to_user(NDCX, Xm, XM, NDCY, Ym, YM)

print("\nValores calculados: \n")
print(f"NDCX: {NDCX:.3f}, NDCY: {NDCY:.3f}, DCX: {DCX}, DCY: {DCY}")
print(f"Usando user_to_ndc: ndcx = {user_to_ndc[0]:.3f}, ndcy = {user_to_ndc[1]:.3f}")
print(f"Usando ndc_to_user: X = {ndc_to_user[0]:.3f}, Y = {ndc_to_user[1]:.3f}")
tela = tk.Tk()

canvas = tk.Canvas(tela, width=NDH, height=NDV, relief="solid", bd=2)
canvas.pack(padx=3, pady=3)

yDoPixel = NDV - DCY #Vertical (dado pelo usuário) - Altura da Tela (calculado)

canvas.create_rectangle(DCX, yDoPixel, DCX + 1, yDoPixel + 1, fill="black")

tela.mainloop()

'''
Para copiar

200
150
100
300
100
200
800
600

------------------

Coordenadas do mundo: [200, 150]
Janela do mundo: Limite em X [200, 100] | Limite em Y [200, 100]
Tamanho da jnaela: 800 X 600
'''