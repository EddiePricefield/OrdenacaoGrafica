package template;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import br.com.davidbuzatto.jsge.imgui.GuiButton;
import br.com.davidbuzatto.jsge.imgui.GuiTextField;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Modelo de projeto básico da JSGE.
 * 
 * JSGE basic project template.
 * 
 * @author Prof. Dr. David Buzatto
 */
public class Main extends EngineFrame {
    
    //Variável para os arrays
    private int[] array;
    
    //Valor para a mensagem dependendo da situação
    private int msg;
    
    //Listas com os passos de cada um dos algoritmos de ordenação
    private List<int[]> selectionArrays;
    private List<int[]> insertionArrays;
    private List<int[]> shellArrays;
    private List<int[]> mergeArrays;
    
    //Valores para indicar qual dos passos ir no momento
    private int copiaSelectionAtual;
    private int copiaInsertionAtual;
    private int copiaShellAtual;
    private int copiaMergeAtual;
    
    //Criação do "cronometro"
    private double tempoParaMudar;
    private double contadorTempo;
    
    //Valores para medir a velocidade em tempo real das ordenações
    private double tempoSelection;
    private double tempoInsertion;
    private double tempoShell;
    private double tempoMerge;
    
    //Caixa de texto para inserir valores de array para realizar a simulação
    private GuiTextField txtCaixa;
    
    //Botão para reiniciar a simulação inteira
    private GuiButton btnResetAll;
    
    public Main() {
        
        super(
            800,                 // largura                      / width
            450,                 // algura                       / height
            "Ordenações",      // título                       / title
            60,                  // quadros por segundo desejado / target FPS
            true,                // suavização                   / antialiasing
            false,               // redimensionável              / resizable
            false,               // tela cheia                   / full screen
            false,               // sem decoração                / undecorated
            false,               // sempre no topo               / always on top
            false                // fundo invisível              / invisible background
        );
        
    }
    
    /**
     * Cria o mundo do jogo.
     * Esse método executa apenas uma vez durante a inicialização da engine.
     * 
     * Creates the game world.
     * This method runs just one time during engine initialization.
     */
    @Override
    public void create() {
        
        useAsDependencyForIMGUI(); //Precisa disso aqui pra fazer os botões funcionarem
        
        array = new int[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        
        selectionArrays = new ArrayList<>();
        insertionArrays = new ArrayList<>();
        shellArrays = new ArrayList<>();
        mergeArrays = new ArrayList<>();
        
        aplicarAlgoritmos( array );
        
        txtCaixa = new GuiTextField( 200, 360, 215, 30, "1, 2, 3, 4, 5, 6, 7, 8, 9, 10" );
        
        btnResetAll = new GuiButton( 425, 360, 160, 30, "Reiniciar a Simulação" );
        
        tempoParaMudar = 0.50;

    }

    /**
     * Lê a entrada do usuário e atualiza o mundo do jogo.
     * Os métodos de entrada devem ser usados aqui.
     * Atenção: Você NÃO DEVE usar nenhum dos métodos de desenho da engine aqui.
     * 
     * 
     * Reads user input and update game world.
     * Input methods should be used here.
     * Warning: You MUST NOT use any of the engine drawing methods here.
     * 
     * @param delta O tempo passado, em segundos, de um quadro para o outro.
     * Time passed, in seconds, between frames.
     */
    @Override
    public void update( double delta ) {
        
        contadorTempo += delta;
        
        //Fazer a mudança dos gráficos conforme o passar do tempo
        if ( contadorTempo >= tempoParaMudar ) {
            
            contadorTempo = 0;
            
            if ( copiaSelectionAtual < selectionArrays.size() - 1 ){ copiaSelectionAtual++; }
            if ( copiaInsertionAtual < insertionArrays.size() - 1 ){ copiaInsertionAtual++; }
            if ( copiaShellAtual < shellArrays.size() - 1 ){ copiaShellAtual++; }
            if ( copiaMergeAtual < mergeArrays.size() - 1 ){ copiaMergeAtual++; }
                
        }

        //Conferir o estado do botão de reiniciar simulação e da caixa de texto
        btnResetAll.update( delta );
        txtCaixa.update ( delta );
        
        //Resetar as posições dos arraysCopia para 0
        if ( btnResetAll.isMousePressed() ) {
            
            //Pegando os valores do TextField e transformando para uma tabela do tipo int
            String[] valoresBrutos = txtCaixa.getValue().split( "[, ]+" ); //Aprendi isso pesquisando um jeito de pegar tanto espaço quanto vírgula
            int[] valores = new int[valoresBrutos.length];

            if( valores.length > 10 ){
                System.err.println( "Mais de 10 valores inseridos para criacao do novo array!!" );
                msg = 1;
            }else{
                
                if( valoresBrutos.length == 0 || valoresBrutos[0].equals( "" ) ){
                    System.err.println( "Array vazio ou com valor nulo em seu primeiro indice" );
                    msg = 3;
                    return;
                }
                
               for( int i = 0; i < valores.length; i++ ){
                   
                   if( Character.isDigit( valoresBrutos[i].charAt( 0 ) ) ){
                       valores[i] = Integer.parseInt( valoresBrutos[i] ); //Usar isso daqui pra transformar de String para Int
                   }else{
                       System.err.println( "Foi encontrado uma letra/caractere especial entre os valores do array!" );
                       msg = 4;
                       return;
                   }
                   
                   
                   if( valores[i] > 99){
                       System.err.println( "Valor acima de 99 inserido entre os valores do array!" );
                       msg = 2;
                       return;
                   }
                   
                }
               
               msg = 5;
               
               //Limpar as listas de array para redesenhar do jeito certo
                insertionArrays.clear();
                selectionArrays.clear();
                shellArrays.clear();
                mergeArrays.clear();
            
                //Aplicar os algoritmos usando a medição do tempo e desenhando o tempo
                aplicarAlgoritmos( valores );
                desenharTempo();
            
                copiaSelectionAtual = 0;
                copiaInsertionAtual = 0;
                copiaShellAtual = 0;
                copiaMergeAtual = 0;
               
            }

        }
                
    }
    
    /**
     * Desenha o mundo do jogo.
     * Todas as operações de desenho DEVEM ser feitas aqui.
     * 
     * Draws the game world.
     * All drawing related operations MUST be performed here.
     */
    @Override
    public void draw() {
        
        //Plano de Fundo
        clearBackground( WHITE );
        
        //Título do Programa
        drawText("Simulação de Algoritmos de Ordenação", 25, 25, 35, BLACK);
        
        //Desenhando os gráficos
        desenharArray( selectionArrays.get( copiaSelectionAtual ), 25, ( getScreenHeight() / 2 ), 10, 5, BLUE );
        desenharArray( insertionArrays.get( copiaInsertionAtual ), 225, ( getScreenHeight() / 2 ), 10, 5, RED );
        desenharArray( shellArrays.get( copiaShellAtual ), 425, ( getScreenHeight() / 2 ), 10, 5, GREEN );
        desenharArray( mergeArrays.get( copiaMergeAtual ), 625, ( getScreenHeight() / 2 ), 10, Arrays.stream( mergeArrays.get(0) ).max().getAsInt() , 5, ORANGE );
        
        //Retângulos ao redor dos gráficos
        drawRectangle( 20, 100, 155, 130, BLACK );
        drawRectangle( 220, 100, 155, 130, BLACK );
        drawRectangle( 420, 100, 155, 130, BLACK );
        drawRectangle( 620, 100, 155, 130, BLACK );
        
        //Retângulo ao redor do botão de reiniciar simulação
        drawRectangle( 180, 345, 425, 60, BLACK );
        
        //Texto com a aba de alteração do array
        drawText( "Insira um novo valor para o array", 155, 280, 25, BLACK );
        drawText( "(Até 10 valores, no intervalo de 0 a 99, separados por vírgula ou espaço)", 110, 315, 13, BLACK );
        
        //Textos com o nome dos Algoritmos
        drawText( "Selection Sort", 35, 85, 15, BLUE );
        drawText( "Insertion Sort", 235, 85, 15, RED );
        drawText( "Shell Sort", 450, 85, 15, GREEN );
        drawText( "Merge Sort", 655, 85, 15, ORANGE );
        
        //Texto com as mensagens de erro
        switch (msg) { //O próprio NetBeans sugeriu para mim usar um "rule switch" e fez as alterações
            case 1 -> drawText( "Erro: Insira no máximo 10 valores para o novo array!", 165, 415, 15, RED );
            case 2 -> drawText( "Erro: Insira apenas valores entre 0 a 99!", 210, 415, 15, RED );
            case 3 -> drawText( "Erro: Insira ao menos um valor no primeiro indice para criar um novo array!", 65, 415, 15, RED );
            case 4 -> drawText( "Erro: Insira apenas números!", 270, 415, 15, RED );
            case 5 -> drawText( "Simulação realizada com sucesso!", 250, 415, 15, GREEN );
            default -> {
            }
        }
        
        //Tempo de execução dos algoritmos de ordenação
        desenharTempo();
        
        //Desenhar caixa de preenchimento e botão de reiniciar
        txtCaixa.draw();
        btnResetAll.draw();
        
    }
    
    //Implementando os Algoritmos de Ordenação (a implementação estava disponível nos próprios Slides)
    
    private void selectionSort( int[] array ) {
        
        for ( int i = 0; i < array.length; i++ ) {
            
            int min = i;
            for ( int j = i + 1; j < array.length; j++ ) {
                if ( array[j] < array[min] ) {
                    min = j;
                }
            }
            
            if ( selectionArrays.isEmpty() || !Arrays.equals( array, selectionArrays.get(selectionArrays.size() - 1)) ){
                copiarArray( array, selectionArrays );
            }
            
            trocar( array, i, min );
            
        }
        
        if ( selectionArrays.isEmpty() || !Arrays.equals( array, selectionArrays.get(selectionArrays.size() - 1)) ){
           copiarArray( array, selectionArrays );
        }
        
    }
    
    private void insertionSort( int[] array ) {
        
        for ( int i = 1; i < array.length; i++ ) {
            
            int j = i;
            
            while (j > 0 && array[j-1] > array[j]) {
                
                if ( insertionArrays.isEmpty() || !Arrays.equals( array, insertionArrays.get(insertionArrays.size() - 1)) ){
                    copiarArray( array, insertionArrays );
                }
                
                trocar ( array, j-1, j );
                j--;
            }
            
            if ( insertionArrays.isEmpty() || !Arrays.equals( array, insertionArrays.get(insertionArrays.size() - 1)) ){
                copiarArray( array, insertionArrays );
            }
            
        }
        
        if ( insertionArrays.isEmpty() || !Arrays.equals( array, insertionArrays.get(insertionArrays.size() - 1)) ){
            copiarArray( array, insertionArrays );
        }
        
    }
    
    private void shellSort( int[] array){

        int h = 1;
        
        while ( h < array.length / 3 ) {
            h = 3 * h + 1;
        }

        while ( h >= 1 ) {

            for ( int i = h; i < array.length; i++ ){
            
                int j = i;

                while ( j >= h && array[j-h] > array[j] ) {
                    
                    if ( shellArrays.size() == 0 || !Arrays.equals( array, shellArrays.get(shellArrays.size() - 1)) ){
                        copiarArray( array, shellArrays );
                    }
                    
                    trocar( array, j-h, j );
                    j = j - h;
                }
            }

            if ( shellArrays.size() == 0 || !Arrays.equals( array, shellArrays.get(shellArrays.size() - 1)) ){
               copiarArray( array, shellArrays );
            }
            
            h = h / 3;

        }
        
        copiarArray( array, shellArrays );

    }
    
    private void mergeSort( int[] array ) {
        
        copiarArray( array, mergeArrays );
        int length = array.length;
        int[] tempMS = new int[length];
        topDown( array, 0, length - 1, tempMS );
                
    }
    
    private void topDown( int[] array, int start, int end, int[] tempMS ) {

        int middle;

        if ( start < end ) {
            middle = ( start + end ) / 2;
            topDown( array, start, middle, tempMS ); // esquerda
            topDown( array, middle + 1, end, tempMS ); // direita
            merge( array, start, middle, end, tempMS ); // intercalação
        }
    }
    
    private void merge( int[] array, int start, int middle, int end, int[] tempMS ) {

        int i = start;
        int j = middle + 1;

        for ( int k = start; k <= end; k++ ) {
            tempMS[k] = array[k];
        }

        for ( int k = start; k <= end; k++ ) {

            if ( i > middle ) {
                array[k] = tempMS[j++];
            } else if ( j > end ) {
                array[k] = tempMS[i++];
            } else if ( tempMS[j] < tempMS[i] ) {
                array[k] = tempMS[j++];
            } else {
                array[k] = tempMS[i++];
            }
         
            // Achei uma maneira de registrar só se o passo atual for diferente do anterior, mas não sei se seria bom usar ou não
            if ( mergeArrays.isEmpty() || !Arrays.equals( array, mergeArrays.get(mergeArrays.size() - 1)) ){
                copiarArray( array, mergeArrays );
            }            
         
        }
        
    }
    
    //Algoritmos Complementares
    
    private void trocar( int[] array, int i, int min ) {
        int t = array[i];
        array[i] = array[min];
        array[min] = t;
    }
    
    private void copiarArray( int[] array, List<int[]> listaDeArrays ) {
        int[] copia = new int[array.length];
        System.arraycopy( array, 0, copia, 0, array.length );
        listaDeArrays.add( copia );
    }
    
    private void desenharTempo(){
        drawText( String.format( "%.5f ms", tempoSelection/1000000 ), 55, 240, 15, BLUE );
        drawText( String.format( "%.5f ms", tempoInsertion/1000000 ), 255, 240, 15, RED );
        drawText( String.format( "%.5f ms", tempoShell/1000000 ), 455, 240, 15, GREEN );
        drawText( String.format( "%.5f ms", tempoMerge/1000000 ), 655, 240, 15, ORANGE );
    }
    
    private void aplicarAlgoritmos( int[] array ){
        
        long tempoInicial; //Não sabia da existência do "nanoTime", ele é bom de usar
        
        tempoInicial = System.nanoTime();
        selectionSort( array.clone() );
        tempoSelection = ( System.nanoTime() - tempoInicial );
        
        tempoInicial = System.nanoTime();
        insertionSort( array.clone() );
        tempoInsertion = ( System.nanoTime() - tempoInicial );
        
        tempoInicial = System.nanoTime();
        shellSort( array.clone() );
        tempoShell = ( System.nanoTime() - tempoInicial );
        
        tempoInicial = System.nanoTime();
        mergeSort( array.clone() );
        tempoMerge = ( System.nanoTime() - tempoInicial );
        
    }
       
    //Desenhando o gráfico
    
    private void desenharArray( int[] a, int xIni, int yIni, int tamanho, int espaco, Color cor ) {
        
        for ( int i = 0; i < a.length; i++ ) {
            
            int max = Arrays.stream( a ).max().getAsInt(); //Confeso que não fazia ideia da existencia disso daqui, mas fez o que eu queria
            double altura = ( a[i] * ( 120.0 / max ) ); //usando o valor maximo do meu retângulo e do array pra criar uma escala pra altura
            
            fillRectangle(
                    xIni + ( tamanho + espaco ) * i,
                    yIni - altura,
                    tamanho,
                    altura, cor
            );
            
        }
        
    }
    
    //Preciso criar um outro método exclusivo pro MergeSort, senão a parte visual dele vai ficar toda feia comparada com as outras. 
    
    private void desenharArray(int[] a, int xIni, int yIni, int tamanho, int max, int espaco, Color cor) { //Mas ai é só eu passar o valor maximo do array primário e usar ele como base pra todo o resto

        for (int i = 0; i < a.length; i++) {

            double altura = ( a[i] * ( 120.0 / max ) ); //usando o valor maximo do meu retângulo e do array pra criar uma escala pra altura

            fillRectangle(
                    xIni + (tamanho + espaco) * i,
                    yIni - altura,
                    tamanho,
                    altura, cor
            );

        }

    }
    
    /**
     * Instancia a engine e a inicia.
     * 
     * Instantiates the engine and starts it.
     */
    public static void main( String[] args ) {
        new Main();
    }
    
} 
