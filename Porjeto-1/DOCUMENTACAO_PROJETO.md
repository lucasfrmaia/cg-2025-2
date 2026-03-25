# Documentação Completa - Projeto de Computação Gráfica

## 📋 Índice

1. [Visão Geral do Projeto](#visão-geral-do-projeto)
2. [Arquitetura Geral](#arquitetura-geral)
3. [Estrutura de Diretórios](#estrutura-de-diretórios)
4. [Componentes Principais](#componentes-principais)
5. [Fluxo de Execução](#fluxo-de-execução)
6. [Módulos Detalhados](#módulos-detalhados)

---

## 🎯 Visão Geral do Projeto

Este é um **projeto de Computação Gráfica e Processamento de Imagem** desenvolvido em Java. Ele implementa diversos algoritmos e técnicas gráficas, incluindo:

- **Primitivas Gráficas**: Linhas, círculos e elipses usando diferentes algoritmos
- **Transformações 2D**: Rotação, translação, escala, cisalhamento e reflexão
- **Transformações 3D**: Transformações geométricas em espaço 3D
- **Simulação de ECG**: Visualização de sinais cardíacos em tempo real
- **Sistema de Viewport**: Janelas de visualização para planos cartesianos

---

## 🏗️ Arquitetura Geral

A arquitetura segue o padrão **MVC (Model-View-Controller)** com separação clara entre:

```
┌─────────────────────────────────────────────────────────┐
│                    CAMADA DE APRESENTAÇÃO (View)       │
│  MainScreen | SelectOptions | InputsPanel              │
└────────────────────────┬────────────────────────────────┘
                         │
┌────────────────────────┴────────────────────────────────┐
│              CAMADA DE GERENCIAMENTO (Control)          │
│  JPanelHandler | GeometricFiguresHandler               │
│  DataOptions | SelectOptions                            │
└────────────────────────┬────────────────────────────────┘
                         │
┌────────────────────────┴────────────────────────────────┐
│              CAMADA DE LÓGICA (Model)                   │
│  Primitives | Transformations2D | Geometry             │
│  Transformations3D | ECGSimulation                      │
└─────────────────────────────────────────────────────────┘
```

---

## 📁 Estrutura de Diretórios

```
src/
├── Main.java                                    # Ponto de entrada da aplicação
├── utils/
│   └── Constants.java                          # Constantes globais (cores, dimensões)
├── project_cg/
│   ├── drivers/                                # Interfaces de entrada 3D
│   │   ├── Main3DViewer.java
│   │   ├── clipping/
│   │   │   └── CohenSutherlandLineClipper.java
│   │   ├── tudo3D/                            # Transformações 3D
│   │   ├── viewport3d/
│   │   │   └── Viewport3D.java
│   │   └── viewportNew/
│   │       └── Transformations2DViewport.java
│   ├── geometry/                               # Estruturas geométricas
│   │   ├── figures/
│   │   │   ├── BaseFigure.java
│   │   │   ├── Circle.java
│   │   │   ├── Ellipse.java
│   │   │   ├── Line.java
│   │   │   └── Square.java
│   │   ├── planeCartesians/                   # Planos cartesianos
│   │   ├── points/
│   │   │   └── Point2D.java
│   ├── ecgSimulator/
│   │   └── ECGSimulation.java                 # Simulação de ECG
│   ├── inputsPanel/                           # Painéis de entrada
│   ├── pixel/
│   │   └── Coordinates.java
│   ├── primitives/                            # Algoritmos de primitivas
│   │   ├── CircleExplicit.java
│   │   ├── DDALine.java
│   │   ├── MidpointCircle.java
│   │   ├── MidpointElipse.java
│   │   ├── MidpointLine.java
│   │   ├── TrigometricCircle.java
│   │   └── bases/
│   │       ├── BasePrimitives.java
│   │       ├── BaseCircle.java
│   │       ├── BaseEllipse.java
│   │       └── BaseLine.java
│   └── transformations2d/                     # Transformações 2D
│       ├── Reflection.java
│       ├── Rotation.java
│       ├── Scale.java
│       ├── Shear.java
│       └── Translation.java
└── view/
    ├── mainScreen/
    │   ├── MainScreen.java                    # Janela principal
    │   ├── MainScreenSingleton.java          # Padrão Singleton
    │   └── mainScreenPanels/
    │       └── InputsPanel.java              # Painel de entrada
    ├── select/
    │   └── SelectOptions.java                 # Seletor de opções
    └── utils/
        ├── BaseJPanel.java                    # Base para painéis
        ├── DataOptions.java                   # Gerenciador de opções
        ├── GeometricFiguresHandler.java      # Handler de figuras
        ├── JPanelHandler.java                 # Handler de painéis
        ├── Matrix.java                        # Operações matriciais
        ├── OptionDisabled.java
        └── ShapePanel.java                    # Base para painéis de formas
```

---

## 🔑 Componentes Principais

### 1. **Main.java** - Ponto de Entrada

```java
Responsabilidades:
- Inicializar a aplicação
- Criar a tela principal (MainScreen)
- Configurar o painel de entrada (InputsPanel)
- Exibir a janela
```

---

### 2. **view/mainScreen/MainScreen.java** - Janela Principal

**Propósito**: Gerenciar a interface gráfica principal da aplicação

**Responsabilidades**:

- Criar e manter a JFrame principal
- Gerenciar layout com GridBagLayout
- Integrar painéis cartesianos e inputs
- Alternar entre diferentes modos de visualização

**Métodos Principais**:

- `setInputs()` - Define o painel de entrada
- `setLayoutPanel()` - Configura o layout
- `updateCurrentPanel()` - Muda o painel visualizado
- `resetCartesianPlane()` - Reseta a visualização

---

### 3. **view/mainScreen/MainScreenSingleton.java** - Singleton

**Propósito**: Garantir uma única instância de MainScreen

**Padrão**: Singleton

- Fornece acesso global a `MainScreen`
- Impede múltiplas instâncias

---

### 4. **view/utils/JPanelHandler.java** - Gerenciador de Painéis

**Propósito**: Gerenciar múltiplos painéis cartesianos

**Responsabilidades**:

- Armazenar diferentes tipos de planos cartesianos
- Alternar entre categorias (Primitivas, Transformações, etc)
- Fornecer acesso tipado aos planos (2D, 3D, ECG)

**Estrutura Interna**:

```java
Map<String, BaseJPanel> cartesiansPlane
- "Primitivas" → CartesianPlane2D
- "Transformações" → CartesianPlane2DWithViewport
- "Pixel" → PixelCartesianPlane
- "Simulador de Coração" → ECGSimulation
- "Plano 3D" → CartesianPlane3D
```

---

### 5. **view/utils/DataOptions.java** - Gerenciador de Opções

**Propósito**: Organizar opções em categorias e subcategorias

**Estrutura de Dados**:

```
Map<Categoria, Map<Opção, ShapePanel>>
  ├── Transformações
  │   ├── Desenhar Quadrado → CreatePolygonInputs
  │   ├── Aplicar Rotação → RotationInputs
  │   ├── Aplicar Escala → ScaleInputs
  │   └── ...
  ├── Primitivas
  │   ├── DDA → DDALineInputs
  │   ├── Círculo Explícito → CircleExplicitInputs
  │   └── ...
  └── ...
```

---

### 6. **view/utils/ShapePanel.java** - Base para Painéis de Entrada

**Propósito**: Fornecer template para painéis de entrada

**Responsabilidades**:

- Gerenciar layout com GridBagLayout
- Adicionar campos de entrada (text fields, combo boxes)
- Gerenciar botão "Calcular"

**Métodos Abstratos**:

- `initializeInputs()` - Criação de componentes
- `onCalculate()` - Lógica ao calcular

---

### 7. **project_cg/geometry/points/Point2D.java** - Ponto 2D

**Propósito**: Representar um ponto no plano 2D

**Atributos**:

- `x` - Coordenada X
- `y` - Coordenada Y

**Métodos**:

- `updatePoint()` - Atualizar coordenadas
- `getX()`, `getY()` - Getters
- `toString()` - Representação em string

---

### 8. **project_cg/geometry/figures/BaseFigure.java** - Base para Figuras

**Propósito**: Padrão abstrato para todas as figuras geométricas

**Responsabilidades**:

- Armazenar lista de pontos da figura
- Gerenciar cor da figura
- Fornecer métodos para iteração

**Métodos Abstratos**:

- `getID()` - Identificador único
- `generatePoints()` - Gerar pontos da figura

---

## 📦 Módulos Detalhados

### A. PRIMITIVAS GRÁFICAS (`project_cg/primitives/`)

As primitivas são os elementos básicos do desenho. Existem diferentes algoritmos para desenhá-las.

#### **1. BasePrimitives.java** - Base Abstrata

```java
- Armazena callback para processar pontos gerados
- Fornece setter/getter para callback
- Base para todos os algoritmos de primitivas
```

#### **2. BaseLine.java** - Base para Linhas

```java
- Estende BasePrimitives
- Define interface desenhaLinha(Point2D, Point2D)
```

#### **3. DDALine.java** - Algoritmo DDA (Digital Differential Analyzer)

**Algoritmo**: Incremento diferencial digital

```
Passos:
1. Calcular Δx e Δy
2. Determinar número de passos = max(|Δx|, |Δy|)
3. Calcular incrementos: xIncrement = Δx/steps, yIncrement = Δy/steps
4. Iterar e plotar pontos

Vantagens:
- Rápido e simples
- Funciona para qualquer inclinação
```

**Exemplo de Uso**:

```java
DDALine dda = new DDALine(point -> plano.setPixel(point, color));
dda.desenhaLinha(new Point2D(0, 0), new Point2D(10, 5));
```

---

#### **4. MidpointLine.java** - Algoritmo Bresenham (Midpoint)

**Algoritmo**: Algoritmo do Ponto Médio para linhas

```
Características:
- Usar apenas aritmética inteira
- Dividido em 8 octantes para eficiência
- Plotar linhas precisamente com uma única unidade de largura

Octantes:
1. dx > 0, dy > 0, |dy| ≤ |dx|   (0° a 45°)
2. dx > 0, dy > 0, |dy| > |dx|   (45° a 90°)
3. dx < 0, dy > 0, |dy| > |dx|   (90° a 135°)
... (8 total)
```

**Vantagens**:

- Mais preciso que DDA
- Usa apenas inteiros
- Otimizado por octante

---

#### **5. MidpointCircle.java** - Algoritmo Midpoint para Círculos

**Algoritmo**: Algoritmo do Ponto Médio para circunferências

```
Processo:
1. Começar no ponto (0, r)
2. Calcular parâmetro decisório d = 1 - r
3. Para cada x < y:
   - Se d < 0: d += 2x + 1
   - Senão: d += 2(x - y) + 1
4. Plotar 8 pontos por simetria (todos os octantes)

Simetria de 8 pontos:
(x, y)    (-x, y)     (x, -y)    (-x, -y)
(y, x)    (-y, x)     (y, -x)    (-y, -x)
```

**Vantagens**:

- Eficiente com simetria
- Suave e contínuo

---

#### **6. CircleExplicit.java** - Círculo por Equação Explícita

**Algoritmo**: $ y = \sqrt{r^2 - x^2} $

```
Processo:
1. Iterar de -r a r
2. Calcular y = sqrt(r² - x²)
3. Plotar (x, y) e (-x, y)

Desvantagem: Lentidão (usa raiz quadrada)
```

---

#### **7. MidpointElipse.java** - Elipse por Ponto Médio

**Algoritmo**: Generalização do círculo para elipses

```
Equação da Elipse: (x²/a²) + (y²/b²) = 1

Características:
- Dividida em 2 regiões
- Cada região tem estratégia diferente
- Simétrica em 4 quadrantes
```

---

### B. TRANSFORMAÇÕES 2D (`project_cg/transformations2d/`)

Transformações geométricas usando **coordenadas homogêneas** e multiplicação de matrizes.

#### **1. Translation.java** - Translação

**Matriz de Translação**:
$$T(t_x, t_y) = \begin{pmatrix} 1 & 0 & 0 \\ 0 & 1 & 0 \\ t_x & t_y & 1 \end{pmatrix}$$

**Aplicação**:

```java
Point2D translated = Translation.translatePoint(point, tx, ty);
```

---

#### **2. Rotation.java** - Rotação

**Matriz de Rotação** (em radianos):
$$R(\theta) = \begin{pmatrix} \cos\theta & -\sin\theta & 0 \\ \sin\theta & \cos\theta & 0 \\ 0 & 0 & 1 \end{pmatrix}$$

**Aplicação**:

```java
Point2D rotated = Rotation.rotatePoint(point, angleInDegrees);
```

---

#### **3. Scale.java** - Escala

**Matriz de Escala**:
$$S(s_x, s_y) = \begin{pmatrix} s_x & 0 & 0 \\ 0 & s_y & 0 \\ 0 & 0 & 1 \end{pmatrix}$$

**Aplicação**:

```java
Point2D scaled = Scale.scalePoint(point, sx, sy);
```

---

#### **4. Reflection.java** - Reflexão

**Matrizes de Reflexão**:

Reflexão em X:
$$R_x = \begin{pmatrix} 1 & 0 & 0 \\ 0 & -1 & 0 \\ 0 & 0 & 1 \end{pmatrix}$$

Reflexão em Y:
$$R_y = \begin{pmatrix} -1 & 0 & 0 \\ 0 & 1 & 0 \\ 0 & 0 & 1 \end{pmatrix}$$

Reflexão na Origem:
$$R_o = \begin{pmatrix} -1 & 0 & 0 \\ 0 & -1 & 0 \\ 0 & 0 & 1 \end{pmatrix}$$

---

#### **5. Shear.java** - Cisalhamento

**Matriz de Cisalhamento**:
$$H(h_x, h_y) = \begin{pmatrix} 1 & h_x & 0 \\ h_y & 1 & 0 \\ 0 & 0 & 1 \end{pmatrix}$$

---

### C. PLANOS CARTESIANOS (`project_cg/geometry/planeCartesians/`)

#### **1. BaseCartesianPlane2D.java** - Base Abstrata

**Propósito**: Interface para operações de desenho em 2D

**Métodos Principais**:

- `setPixel(Point2D, int rgb)` - Definir cor de um pixel
- `getPixel(int x, int y)` - Obter cor de um pixel
- `drawCartesianPlane()` - Desenhar eixos cartesianos
- `clear()` - Limpar tela
- `reset()` - Resetar para novo plano

---

#### **2. CartesianPlane2D.java** - Plano 2D Padrão

**Características**:

- Converte coordenadas matemáticas para coordenadas de tela
- Origem no centro da tela
- Eixo Y cresce para cima

**Conversão de Coordenadas**:

```
screenX = x + width/2
screenY = height/2 - y
```

---

#### **3. CartesianPlane2DWithViewport.java** - Com Viewport

**Características**:

- Suporta janela de visualização (viewport)
- Permite zoom e pan
- Visualiza apenas região da viewport

**ViewportWindow**:

- Rastreia limites de visualização
- Coordenadas da window vs. world

---

#### **4. PixelCartesianPlane.java** - Modo Pixel

**Características**:

- Operações pixel a pixel
- Modo específico para manipulação direta

---

### D. SIMULAÇÃO DE ECG (`project_cg/ecgSimulator/`)

#### **ECGSimulation.java** - Simulador de Sinais Cardíacos

**Propósito**: Visualizar sinais de ECG em tempo real

**Características**:

- Estende `CartesianPlane2D`
- Gera dados simulados de ECG
- Renderiza como gráfico de linha

**Atributos**:

```java
- ecgData: Lista<Integer>       // Valores do sinal
- maxDataPoints: int             // Máximo de pontos visualizados
- timer: Timer                   // Animação
- animationDuration: int         // Duração da animação
```

**Algoritmo de Desenho**:

1. Limpa a tela com fundo verde escuro
2. Para cada ponto ECG:
   - Calcula posição x baseada no índice
   - Calcula posição y baseada no valor
   - Desenha linha usando MidpointLine
3. Renderiza usando setPixel()

---

### E. UTILIDADES (`view/utils/`)

#### **Matrix.java** - Operações Matriciais

**Métodos Estáticos**:

- `add(a, b)` - Adição de matrizes
- `subtract(a, b)` - Subtração
- `multiply(a, b)` - Multiplicação
- `transpose(matrix)` - Transposição
- `printMatrix(matrix)` - Exibição

**Uso em Transformações**:

```
Transformações são aplicadas via:
resultado = ponto_homogêneo × matriz_transformação
```

---

#### **GeometricFiguresHandler.java** - Gerenciador de Figuras

**Propósito**: Gerenciar figuras geométricas criadas

**Responsabilidades**:

- Armazenar figuras criadas
- Fornecer combo box de figuras
- Aplicar transformações às figuras

---

#### **Constants.java** - Constantes Globais

```java
WIDTH_MAIN_SCREEN = 1300        // Largura da janela
HEIGHT_MAIN_SCREEN = 860        // Altura da janela
WIDTH_CARTESIAN_PLANE = 1287    // 99% da largura
HEIGHT_CARTESIAN_PLANE = 731    // 85% da altura

COLOR_LINES_CARTESIAN_PLANE = WHITE     // Cor dos eixos
BACKGROUND_CARTESIAN_PLANE = BLACK      // Fundo
COLOR_PRIMITEVES = RED                  // Cor das primitivas
```

---

## 🔄 Fluxo de Execução

### 1. Inicialização

```
Main.main()
  ↓
MainScreenSingleton.getMainScreen()    [Cria MainScreen]
  ↓
MainScreen.setInputs(InputsPanel)       [Adiciona painel de entrada]
  ↓
MainScreen.setVisible(true)             [Exibe janela]
```

### 2. Seleção de Categoria

```
SelectOptions (ComboBox 1) → Categoria
  ↓
DataOptions.getSecondComboBoxOptions()  [Obtém opções da categoria]
  ↓
SelectOptions (ComboBox 2) → Opção
```

### 3. Execução de Operação

```
Botão "Calcular" em ShapePanel
  ↓
ShapePanel.onCalculate()                [Implementação específica]
  ↓
Modifica CartesianPlane                 [Desenha resultado]
  ↓
JPanelHandler.getCurrentPanel().repaint()
```

### 4. Desenho de Primitivas

```
DDALineInputs.onCalculate()
  ↓
Cria DDALine com callback
  ↓
desenhaLinha(start, end)
  ↓
Para cada ponto gerado:
  callback.accept(point)
    ↓
    CartesianPlane2D.setPixel(point, color)
  ↓
CartesianPlane2D.repaint()              [Renderiza na tela]
```

---

## 🎨 Fluxo de Transformações

### Aplicação de Transformação a uma Figura

```
RotationInputs.onCalculate()
  ↓
Obter figura selecionada do GeometricFiguresHandler
  ↓
Para cada ponto da figura:
  Rotation.rotatePoint(point, angle)
    ↓
    Criar matriz de rotação
    ↓
    Multiplicar ponto_homogêneo × matriz
    ↓
    Retornar novo ponto
  ↓
Desenhar figura com novos pontos
  ↓
CartesianPlane.repaint()
```

---

## 🏛️ Padrões de Design Utilizados

### 1. **Singleton Pattern**

- `MainScreenSingleton` - Uma única instância de MainScreen

### 2. **Strategy Pattern**

- Diferentes algoritmos de primitivas (DDA, Midpoint, etc)
- Diferentes transformações (Rotation, Scale, etc)

### 3. **Template Method Pattern**

- `ShapePanel` - Estructura padrão para painéis de entrada
- `BaseFigure` - Interface comum para figuras
- `BaseCartesianPlane2D` - Interface comum para planos

### 4. **Observer Pattern**

- Callbacks em primitivas para processar pontos gerados

### 5. **Factory Pattern**

- `DataOptions` cria e organiza painéis de entrada

---

## 🎯 Fluxos Principais de Funcionalidade

### Desenhar uma Linha (DDA)

```java
1. Usuário seleciona "Primitivas" → "DDA"
2. InputsPanel cria DDALineInputs
3. Usuário insere coordenadas
4. Clica "Calcular"
5. DDALineInputs.onCalculate():
   - Obtém CartesianPlane2D do JPanelHandler
   - Cria DDALine com callback para setPixel
   - Chama desenhaLinha(start, end)
   - Cada ponto gerado → callback.accept(point)
   - setPixel renderiza o ponto
6. CartesianPlane2D.repaint() exibe o resultado
```

### Aplicar Rotação a um Quadrado

```java
1. Usuário desenha quadrado (Transformações → Desenhar Quadrado)
2. CreatePolygonInputs cria e armazena no GeometricFiguresHandler
3. Usuário seleciona Transformações → Aplicar Rotação
4. RotationInputs.onCalculate():
   - Obtém figura do GeometricFiguresHandler
   - Para cada ponto: novo_ponto = Rotation.rotatePoint(ponto, ângulo)
   - Cria nova figura com pontos rotacionados
   - Desenha na CartesianPlane
5. Resultado visualizado
```

### Visualizar ECG

```java
1. Usuário seleciona "Simulador de Coração"
2. InputsPanel ativa ECGSimulation
3. ECGSimulationInputs.onCalculate():
   - Define duração da simulação
   - Inicia Timer para gerar dados
4. ECGSimulation.paintComponent():
   - Limpa tela com fundo verde escuro
   - Para cada ponto ECG:
     - Desenha linha com MidpointLine
     - Renderiza com setPixel
   - Atualiza continuamente
```

---

## 📊 Resumo de Classes por Camada

### Camada de Apresentação (View)

- `MainScreen.java` - Janela principal
- `InputsPanel.java` - Painel de entrada
- `SelectOptions.java` - Seletor de opções
- `ShapePanel.java` - Base para painéis de forma

### Camada de Controle (Controller)

- `JPanelHandler.java` - Gerencia painéis cartesianos
- `DataOptions.java` - Gerencia opções e submenu
- `GeometricFiguresHandler.java` - Gerencia figuras criadas
- `MainScreenSingleton.java` - Acesso único à tela

### Camada de Modelo (Model)

- `Point2D.java` - Ponto 2D
- `BaseFigure.java` - Base para figuras
- Primitivas (DDALine, MidpointLine, MidpointCircle, etc)
- Transformações (Translation, Rotation, Scale, etc)
- Planos (CartesianPlane2D, CartesianPlane2DWithViewport, etc)
- `ECGSimulation.java` - Simulação de ECG

### Utilidades

- `Matrix.java` - Operações matriciais
- `Constants.java` - Constantes globais

---

## 🚀 Como Adicionar uma Nova Primitiva

1. **Criar classe estendendo `BasePrimitives`**:

   ```java
   public class MyPrimitive extends BasePrimitives {
       public MyPrimitive(Consumer<Point2D> callback) {
           super(callback);
       }

       public void draw(...) {
           // Gerar pontos
           callback.accept(new Point2D(x, y));
       }
   }
   ```

2. **Criar painel de entrada estendendo `ShapePanel`**:

   ```java
   public class MyPrimitiveInputs extends ShapePanel {
       @Override
       protected void initializeInputs() {
           // Adicionar campos de entrada
       }

       @Override
       protected void onCalculate() {
           // Lógica de desenho
       }
   }
   ```

3. **Registrar em `InputsPanel.java`**:
   ```java
   dataOptions.addOption("Primitivas", "Minha Primitiva",
                         new MyPrimitiveInputs());
   ```

---

## 🔧 Tecnologias Utilizadas

- **Linguagem**: Java
- **Interface Gráfica**: Swing (JFrame, JPanel, GridBagLayout)
- **Estrutura de Dados**: ArrayList, LinkedHashMap
- **Padrões**: MVC, Singleton, Strategy, Template Method
- **Matemática**: Algebra Linear (Matrizes), Coordenadas Homogêneas

---

## 📝 Notas Importantes

1. **Coordenadas Homogêneas**: Todas as transformações 2D usam matrizes 3x3 com coordenadas homogêneas
2. **Callback Pattern**: Primitivas usam callbacks para processar pontos gerados
3. **Conversão de Coordenadas**: Plano matemático ↔ Tela de computador
4. **Simetria**: Aproveitada em algoritmos (círculos, elipses)
5. **Inteiros vs Floats**: Primitivas usam inteiros para velocidade; transformações usam doubles

---

## 🎓 Conceitos de Computação Gráfica Implementados

### Algoritmos de Rasterização

- **DDA Line** - Analisador Diferencial Digital
- **Midpoint/Bresenham** - Algoritmo do Ponto Médio
- **Circle Rasterization** - Rasterização de Círculos com Simetria
- **Ellipse Rasterization** - Generalização para Elipses

### Transformações Geométricas

- **Translação** - Deslocamento
- **Rotação** - Giro ao redor de um ponto
- **Escala** - Ampliação/Redução
- **Cisalhamento (Shear)** - Distorção linear
- **Reflexão** - Espelhamento

### Coordenadas Homogêneas

- Representação unificada de transformações
- Combinação de transformações via multiplicação de matrizes
- Permite transformações afins

### Viewport

- Janela de visualização do mundo virtual
- Mapeamento de coordenadas mundo ↔ tela
- Zoom e pan

---

## 📚 Referências Conceituais

Este projeto implementa conceitos de:

- **Computer Graphics: Principles and Practice** (Foley, Van Dam, et al)
- **Algoritmos de Rasterização** (Bresenham, Midpoint)
- **Transformações Geométricas** (Shoemake, Akenine-Möller)
- **Processamento de Sinais** (para ECG)

---

**Documento gerado para apresentação do projeto**
**Data: 18 de março de 2026**
**Versão: 1.0**
