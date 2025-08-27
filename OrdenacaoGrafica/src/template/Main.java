package template;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import br.com.davidbuzatto.jsge.imgui.GuiButton;
import br.com.davidbuzatto.jsge.imgui.GuiButtonGroup;
import br.com.davidbuzatto.jsge.imgui.GuiCheckBox;
import br.com.davidbuzatto.jsge.imgui.GuiComponent;
import br.com.davidbuzatto.jsge.imgui.GuiDropdownList;
import br.com.davidbuzatto.jsge.imgui.GuiGlue;
import br.com.davidbuzatto.jsge.imgui.GuiLabel;
import br.com.davidbuzatto.jsge.imgui.GuiLabelButton;
import br.com.davidbuzatto.jsge.imgui.GuiRadioButton;
import br.com.davidbuzatto.jsge.imgui.GuiSlider;
import br.com.davidbuzatto.jsge.imgui.GuiTextField;
import br.com.davidbuzatto.jsge.imgui.GuiToggleButton;
import br.com.davidbuzatto.jsge.imgui.GuiWindow;
import br.com.davidbuzatto.jsge.math.Vector2;
import java.awt.Color;
import java.awt.Desktop;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

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
    
    //Botões do programa
    private GuiLabelButton btnLink;
    private GuiButton btnResetAll;
    private GuiButton btnConfig;
    private GuiToggleButton btnAltosValores;
    private GuiButton btnIniAltosValores;
    
    //Variáveis para a implementação dos Arrays Aleatórios
    private GuiDropdownList dropdownList;
    private GuiButton btnRandom;
    private List<int[]> randomArrays;
    private double tempoRandom;
    private int copiaRandomAtual;
    
    //Variáveis para a janela de configurações
    private GuiGlue glue;
    private Vector2 previousMousePos;
    private GuiComponent draggedComponent;
    private GuiWindow janelaConfig;
    private GuiCheckBox checkOrdenado;
    private GuiCheckBox checkSeed;
    private GuiSlider velSimulacao;
    private GuiLabel labelVelSimulacao;
    private GuiLabel labelTipoMerge;
    private GuiRadioButton radioIterativo;
    private GuiRadioButton radioRecursivo;
    private GuiButtonGroup buttonGroupMerge;
    
    //Variáveis de memória
    private boolean mergeR;
    private boolean marcadoOrdenado;
    private boolean marcadoSeed;
    private boolean definidor;
    private boolean exibirTempo;
    private boolean exibirTempoRandom;
    
    public Main() {
        
        super(
            800,                 // largura                      / width
            480,                 // altura                       / height
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
        
        mergeR = false;
        
        marcadoOrdenado = false;
        marcadoSeed = false;
        definidor = false;
        exibirTempo = false;
        exibirTempoRandom = false;
        
        tempoParaMudar = 0.55;
        
        array = new int[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        
        selectionArrays = new ArrayList<>();
        insertionArrays = new ArrayList<>();
        shellArrays = new ArrayList<>();
        mergeArrays = new ArrayList<>();
        
        randomArrays = new ArrayList<>();
        
        aplicarAlgoritmos( array );
        
        txtCaixa = new GuiTextField( 200, 370, 215, 30, "1, 2, 3, 4, 5, 6, 7, 8, 9, 10" );
        btnResetAll = new GuiButton( 425, 370, 160, 30, "Reiniciar a Simulação" );
        btnAltosValores = new GuiToggleButton( 610, 440, 140, 30, "Modo Altos Valores" );
        
        btnIniAltosValores = new GuiButton( 241, 370, 300, 30, "Iniciar Simulação de Altos Valores" );
        btnIniAltosValores.setVisible( false );
        
        dropdownList = new GuiDropdownList( 630, 270, 100, 25, List.<String>of( "Selection", "Insertion", "Shell", "Merge" ) );
        dropdownList.setVisible( false );
               
        btnRandom = new GuiButton( 737, 270, 15, 60, "▶" );
        btnRandom.setVisible( false );
        
        btnLink = new GuiLabelButton( 10, 445, 110, 20, "@EddiePricefield" );
        btnLink.setVisible( true );
        
        btnConfig = new GuiButton( 760, 440, 30, 30, "🔧" );
        desenharJanela();
        
        glue.setVisible( false );

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
            
            for( int i = 0; i < 2; i++ ){
               if ( copiaRandomAtual < randomArrays.size() - 1 ){ copiaRandomAtual++; } //Jeito que achei pra fazer isso ser 2x mais rápido que os outros
            }   
                
        }

        //Conferir o estado de todos os itens do GUI
        btnResetAll.update( delta );
        txtCaixa.update( delta );
        btnConfig.update( delta );
        btnAltosValores.update( delta );
        btnIniAltosValores.update( delta );
        
        dropdownList.update( delta );
        btnRandom.update( delta );
        
        janelaConfig.update( delta );
        checkOrdenado.update( delta );
        checkSeed.update( delta );
        velSimulacao.update( delta );
        labelVelSimulacao.update( delta );
        labelTipoMerge.update( delta );
        radioIterativo.update( delta );
        radioRecursivo.update( delta );
        
        btnLink.update( delta );
        
        //Resetar a Simulação
        if ( btnResetAll.isMousePressed() ) {
            
            exibirTempo = true;
            
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
                    } else if( valoresBrutos[i].charAt( 0 ) == '-' && valoresBrutos[i].length() > 1 && Character.isDigit( valoresBrutos[i].charAt( 1 ) ) ){
                       System.err.println( "Nao ha suporte para numeros negativos" );
                       msg = 5;
                       return;
                    } else{
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
               
                msg = 6; //Mensagem de sucesso após realização da simulação
               
                iniciarSimulacao( valores );
                
                //Exibir ou não o Array Ordenado após a realização da simulação
                if( marcadoOrdenado ){
                    txtCaixa.setValue( Arrays.toString( insertionArrays.getLast() ).replace( "[", "" ).replace( "]", "" ).replace(",", "") );
                }
               
            }

        }
        
        //Modo Altos Valores
        txtCaixa.setVisible( !btnAltosValores.isSelected() );
        btnResetAll.setVisible( !btnAltosValores.isSelected() );   
        btnIniAltosValores.setVisible( btnAltosValores.isSelected() );
        dropdownList.setVisible( btnAltosValores.isSelected() );
        btnRandom.setVisible( btnAltosValores.isSelected() );
        checkOrdenado.setEnabled( !btnAltosValores.isSelected() );
        checkSeed.setEnabled( btnAltosValores.isSelected() );
        
        if( btnRandom.isMousePressed() ){
            
            exibirTempoRandom = true;
            
            if( checkSeed.isSelected() ){
                
                Random rnd = new Random( 33550336 );
                
                int[] arrayRandom = new int[590];

                for( int i = 0; i < 590; i++ ) {
                    arrayRandom[i] = rnd.nextInt( 589 );
                }

                iniciarSimulacaoRandom( arrayRandom );
                
            } else{
                
               Random rnd = new Random(); 
               
                int[] arrayRandom = new int[590];

                for( int i = 0; i < 590; i++ ) {
                    arrayRandom[i] = rnd.nextInt( 589 );
                }

                iniciarSimulacaoRandom( arrayRandom );
                
            }
            
            
            
        }
                
        if( btnAltosValores.isMousePressed() ){
            
            exibirTempo = false;
            exibirTempoRandom = false;
            msg = 0; //Remover as mensagens de texto verdinhas embaixo
            
            int[] arrayRandomInicial = new int[590];

            for ( int i = 0; i < 590; i++ ) {
                arrayRandomInicial[i] = i;
            }
            
            if( definidor ){
                
                iniciarSimulacao( array );             
                
                definidor = false;
                
            } else{
                
                int[] a = new int[145];
            
                for( int i = 0; i < 145; i++ ){
                    a[i] = i;
                }
                
                iniciarSimulacao( a ); 
                iniciarSimulacaoRandom( arrayRandomInicial );
                
                definidor = true;
                
            }
            
            
        }
        
        if( btnIniAltosValores.isMousePressed() ){
            
            exibirTempo = true;
            msg = 6;
            
            int[] arrayAltosValores = new int[145];
            
            for( int i = 0; i < 145; i++ ){
                arrayAltosValores[i] = 145 - i;
            }
            
            iniciarSimulacao( arrayAltosValores );
            
        }
        
        //Janela de Configurações do programa
        arrastarJanela();
        marcadoOrdenado = checkOrdenado.isSelected();
        marcadoSeed = checkSeed.isSelected();
        mergeR = !radioIterativo.isSelected();
        mergeR = radioRecursivo.isSelected();
        tempoParaMudar = velSimulacao.getValue() / 100;
        
        if( btnConfig.isMousePressed() ){ 
            desenharJanela();
            glue.setVisible( true );   
        }
        
        if( janelaConfig.isCloseButtonPressed() ) {   
            glue.setVisible( false );
        }
        
        if( janelaConfig.isTitleBarPressed() ) {
            draggedComponent = glue;
        }
        
        //Creditos
        if( btnLink.isMousePressed() ){
            try{ URI link = new URI( "https://github.com/EddiePricefield" );
            Desktop.getDesktop().browse( link );
            } catch ( Exception e ){
                e.printStackTrace();
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
        
        //Retângulos ao redor dos gráficos
        drawRectangle( 20, 100, 155, 130, BLACK );
        drawRectangle( 220, 100, 155, 130, BLACK );
        drawRectangle( 420, 100, 155, 130, BLACK );
        drawRectangle( 620, 100, 155, 130, BLACK );
        
        //Retângulo ao redor do botão de reiniciar simulação
        drawRectangle( 180, 355, 425, 60, BLACK );
        
        double escala = 120.0;
        
        if( !btnAltosValores.isSelected() ){
            
            //Texto com a aba de alteração do array
            drawText( "Insira um novo valor para o array", 155, 280, 25, BLACK );
            drawText( "(Até 10 valores, no intervalo de 0 a 99, separados por vírgula ou espaço)", 110, 315, 13, BLACK );
            
            //Desenhando os gráficos
            desenharArray( selectionArrays.get( copiaSelectionAtual ), 25, 225, 10, 5, escala, BLUE );
            desenharArray( insertionArrays.get( copiaInsertionAtual ), 225, 225, 10, 5, escala, RED );
            desenharArray( shellArrays.get( copiaShellAtual ), 425, 225, 10, 5, escala, GREEN );
            desenharArray( mergeArrays.get( copiaMergeAtual ), 625, 225, 10, 5, Arrays.stream( mergeArrays.get(0) ).max().getAsInt(), escala, ORANGE );
        
            
        } else{
            
            //Desenhando a Função de Array Aleatório
            drawRectangle( 30, 260, 730, 80, BLACK );
            drawRectangle( 630, 305, 100, 25, BLACK );
            drawText( "Randomizer", 650, 345, 15, PINK);                       
            
            //Desenhando o Aleatório
            
            int tamanho = 1, espaco = 0;
            
            if( randomArrays.isEmpty() ){
                
                int[] arrayRandomInicial = new int[590];

                for ( int i = 0; i < 590; i++ ) {
                    arrayRandomInicial[i] = i;
                }
            
                iniciarSimulacaoRandom( arrayRandomInicial );
                
            }
            
            if( !randomArrays.isEmpty() ){
                             
                if (dropdownList.getSelectedItemIndex() == 3) {
                    desenharArray( randomArrays.get(copiaRandomAtual ), 35, 335, tamanho, espaco, Arrays.stream( randomArrays.get( 0 )).max().getAsInt(), 70, PINK );
                } else{
                    desenharArray( randomArrays.get( copiaRandomAtual ), 35, 335, tamanho, espaco, 70, PINK );
                }
                
            }
            
            //Desenhando os gráficos
            desenharArray( selectionArrays.get( copiaSelectionAtual ), 25, 225, tamanho, espaco, escala, BLUE );
            desenharArray( insertionArrays.get( copiaInsertionAtual ), 225, 225, tamanho, espaco, escala, RED );
            desenharArray( shellArrays.get( copiaShellAtual ), 425, 225, tamanho, espaco, escala, GREEN );
            desenharArray( mergeArrays.get( copiaMergeAtual ), 625, 225, tamanho, espaco, Arrays.stream( mergeArrays.get(0) ).max().getAsInt(), escala, ORANGE );
                    
        }
        
        if( exibirTempo ){
            desenharTempo();
        }
        
        if( exibirTempoRandom ){
            drawText( String.format( "%.4fms", tempoRandom/1000000 ), 635, 315, 15, PINK );
        }
        
        //Textos com o nome dos Algoritmos
        drawText( "Selection Sort", 35, 85, 15, BLUE );
        drawText( "Insertion Sort", 235, 85, 15, RED );
        drawText( "Shell Sort", 450, 85, 15, GREEN );
        drawText( "Merge Sort", 655, 85, 15, ORANGE );
        
        //Texto com as mensagens de erro
        
        int y = 425;
        
        switch (msg) { //O próprio NetBeans sugeriu para mim usar um "rule switch" e fez as alterações
            case 1 -> drawText( "Erro: Insira no máximo 10 valores para o novo array!", 165, y, 15, RED );
            case 2 -> drawText( "Erro: Insira apenas valores entre 0 a 99!", 210, y, 15, RED );
            case 3 -> drawText( "Erro: Insira ao menos um valor no primeiro indice para criar um novo array!", 65, y, 15, RED );
            case 4 -> drawText( "Erro: Insira apenas números!", 270, y, 15, RED );
            case 5 -> drawText( "Erro: Não há suporte para números negativos!", 195, y, 15, RED );
            case 6 -> drawText( "Simulação realizada com sucesso!", 250, y, 15, GREEN );
            default -> {
            }
        }
        
        //Desenhar caixa de preenchimento e botões
        txtCaixa.draw();
        btnResetAll.draw();
        btnConfig.draw();
        btnAltosValores.draw();
        btnIniAltosValores.draw();
        
        //Desenhar a parte de Arrays Aleatórios
        dropdownList.draw();
        btnRandom.draw();
        
        //Desenhando a janela de configurações
        janelaConfig.draw();
        checkOrdenado.draw();
        checkSeed.draw();
        velSimulacao.draw();
        labelVelSimulacao.draw();
        btnLink.draw();
        labelTipoMerge.draw();
        radioIterativo.draw();
        radioRecursivo.draw();
        
    }
    
    //Implementando os Algoritmos de Ordenação (a implementação estava disponível nos próprios Slides)
    
    private void selectionSort( int[] array, List<int[]> lista ) {
        
        for ( int i = 0; i < array.length; i++ ) {
            
            int min = i;
            for ( int j = i + 1; j < array.length; j++ ) {
                if ( array[j] < array[min] ) {
                    min = j;
                }
            }
                       
            trocar( array, i, min );
            
            if ( lista.isEmpty() || !Arrays.equals( array, lista.get(lista.size() - 1)) ){
                copiarArray( array, lista );
            }
            
        }
        
        if ( lista.isEmpty() || !Arrays.equals( array, lista.get(lista.size() - 1)) ){
           copiarArray( array, lista );
        }
        
    }
    
    private void insertionSort( int[] array, List<int[]> lista ) {
        
        for ( int i = 1; i < array.length; i++ ) {
            
            int j = i;
            
            while (j > 0 && array[j-1] > array[j]) {
                
                if ( lista.isEmpty() || !Arrays.equals( array, lista.get(lista.size() - 1)) ){
                    copiarArray( array, lista );
                }
                
                trocar ( array, j-1, j );
                j--;
            }
            
            if ( lista.isEmpty() || !Arrays.equals( array, lista.get(lista.size() - 1)) ){
                copiarArray( array, lista );
            }
            
        }
        
        if ( lista.isEmpty() || !Arrays.equals( array, lista.get(lista.size() - 1)) ){
            copiarArray( array, lista );
        }
        
    }
    
    private void shellSort( int[] array, List<int[]> lista ){

        int h = 1;
        
        while ( h < array.length / 3 ) {
            h = 3 * h + 1;
        }

        while ( h >= 1 ) {

            for ( int i = h; i < array.length; i++ ){
            
                int j = i;

                while ( j >= h && array[j-h] > array[j] ) {
                                       
                    trocar( array, j-h, j );
                    j = j - h;
                    
                    if ( lista.size() == 0 || !Arrays.equals( array, lista.get(lista.size() - 1)) ){
                        copiarArray( array, lista );
                    }
                }
            }
            
            h = h / 3;

        }
        
        if ( lista.size() == 0 || !Arrays.equals( array, lista.get(lista.size() - 1)) ){
            copiarArray( array, lista );
        }

    }
    
    private void mergeSort( int[] array, List<int[]> lista ) {
        
        if( mergeR ){
            
            if ( lista.isEmpty() || !Arrays.equals( array, lista.get(lista.size() - 1)) ){
                copiarArray( array, lista );
            }
            
            int length = array.length;
            int[] tempMS = new int[length];
            topDown( array, 0, length - 1, tempMS, lista );
            
        } else{
            
            if ( lista.isEmpty() || !Arrays.equals( array, lista.get(lista.size() - 1)) ){
                copiarArray( array, lista );
            }
            
            int length = array.length;
            int[] tempMS = new int[length];
            bottomUp( array, 0, length - 1, tempMS, lista );
            
        }

    }
                    
    
    private void topDown( int[] array, int start, int end, int[] tempMS, List<int[]> lista ) {

        int middle;

        if ( start < end ) {
            middle = ( start + end ) / 2;
            topDown( array, start, middle, tempMS, lista ); // esquerda
            topDown( array, middle + 1, end, tempMS, lista ); // direita
            merge( array, start, middle, end, tempMS, lista ); // intercalação
        }
    }
    
    
    private void bottomUp( int[] array, int start, int end, int[] tempMS, List<int[]> lista ) {
        for ( int m = 1; m <= end; m *= 2 ) {
            for ( int i = start; i <= end - m; i += 2*m ) {
                merge( array, i, i+m-1, Math.min( i+2*m-1, end ), tempMS, lista );
            }
        }
    }
    
    private void merge( int[] array, int start, int middle, int end, int[] tempMS, List<int[]> lista ) {

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
            if ( lista.isEmpty() || !Arrays.equals( array, lista.get(lista.size() - 1)) ){
                copiarArray( array, lista );
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
        selectionSort( array.clone(), selectionArrays );
        tempoSelection = ( System.nanoTime() - tempoInicial );
        
        tempoInicial = System.nanoTime();
        insertionSort( array.clone(), insertionArrays );
        tempoInsertion = ( System.nanoTime() - tempoInicial );
        
        tempoInicial = System.nanoTime();
        shellSort( array.clone(), shellArrays );
        tempoShell = ( System.nanoTime() - tempoInicial );
        
        tempoInicial = System.nanoTime();
        mergeSort( array.clone(), mergeArrays );
        tempoMerge = ( System.nanoTime() - tempoInicial );
        
    }

    private void aplicarAlgoritmosRandom( int[] array ){
        
        long tempoInicial; //Não sabia da existência do "nanoTime", ele é bom de usar
        
        if( dropdownList.getSelectedItemIndex() == 0 ){
            
            tempoInicial = System.nanoTime();
            selectionSort( array.clone(), randomArrays );
            tempoRandom = ( System.nanoTime() - tempoInicial );
              
        }
        
        if( dropdownList.getSelectedItemIndex() == 1 ){
            
            tempoInicial = System.nanoTime();
            insertionSort( array.clone(), randomArrays );
            tempoRandom = ( System.nanoTime() - tempoInicial );
              
        }
        
        if( dropdownList.getSelectedItemIndex() == 2 ){
            
            tempoInicial = System.nanoTime();
            shellSort( array.clone(), randomArrays );
            tempoRandom = ( System.nanoTime() - tempoInicial );
              
        }
        
        if( dropdownList.getSelectedItemIndex() == 3 ){
            
            tempoInicial = System.nanoTime();
            mergeSort( array.clone(), randomArrays );
            tempoRandom = ( System.nanoTime() - tempoInicial );
              
        }
        
        
    }
    
    private void iniciarSimulacao( int array [] ){
        
        //Limpar as listas de array para redesenhar do jeito certo
        insertionArrays.clear();
        selectionArrays.clear();
        shellArrays.clear();
        mergeArrays.clear();

        //Aplicar os algoritmos usando a medição do tempo e desenhando o tempo
        aplicarAlgoritmos( array );

        //Reiniciar os valores dos gráficos
        copiaSelectionAtual = 0;
        copiaInsertionAtual = 0;
        copiaShellAtual = 0;
        copiaMergeAtual = 0;
        
    }
    
    private void iniciarSimulacaoRandom( int array[] ){
        
        randomArrays.clear();        
        aplicarAlgoritmosRandom( array );        
        copiaRandomAtual = 0;
        
    }
    
    private void arrastarJanela(){
        
        Vector2 mousePos = getMousePositionPoint();
        
        if ( isMouseButtonDown( MOUSE_BUTTON_LEFT ) ) {
            if ( draggedComponent != null ) {
                draggedComponent.move( mousePos.x - previousMousePos.x, mousePos.y - previousMousePos.y );
            }
        } else if ( isMouseButtonUp( MOUSE_BUTTON_LEFT ) ) {
            draggedComponent = null;
        }
        
        previousMousePos = mousePos;
        
    }
    
    private void desenharJanela(){
        
        janelaConfig = new GuiWindow( 295, 150, 200, 220, "Configurações" );
        labelVelSimulacao = new GuiLabel( 20, 40, 40, 20, "Velocidade da Simulação" );
        velSimulacao = new GuiSlider( 20, 40, 130, 60, ( tempoParaMudar * 100 ), 1, 100, GuiSlider.HORIZONTAL );
        checkOrdenado = new GuiCheckBox( 20, 40, 20, 20, " Exibir Array Ordenado" );
        checkSeed = new GuiCheckBox( 20, 40, 20, 20, " Habilitar Seed Fixa" );
        labelTipoMerge = new GuiLabel( 20, 40, 40, 20, "Tipo de MergeSort Usado");
        radioIterativo = new GuiRadioButton(20, 40, 20, 20, "Iterativo");
        radioRecursivo = new GuiRadioButton(20, 40, 20, 20, "Recursivo");
        
        checkOrdenado.setSelected( marcadoOrdenado );
        checkSeed.setSelected( marcadoSeed );
        radioIterativo.setSelected( !mergeR );
        radioRecursivo.setSelected( mergeR );
        
        buttonGroupMerge = new GuiButtonGroup();
        radioIterativo.setButtonGroup( buttonGroupMerge );
        radioRecursivo.setButtonGroup( buttonGroupMerge );
        
        glue = new GuiGlue( janelaConfig );
        glue.addChild( labelVelSimulacao, 20, 35);
        glue.addChild( velSimulacao, 35, 35 );
        glue.addChild( checkOrdenado, 10, 155 );
        glue.addChild( checkSeed, 10, 185 );
        glue.addChild( labelTipoMerge, 20, 85 );
        glue.addChild( radioIterativo, 5, 110 );
        glue.addChild( radioRecursivo, 105, 110 );
            
    }
       
    //Desenhando o gráfico
    
    private void desenharArray( int[] a, int xIni, int yIni, int tamanho, int espaco, double escala, Color cor ) {
        
        for ( int i = 0; i < a.length; i++ ) {
            
            int max = Arrays.stream( a ).max().getAsInt(); //Confeso que não fazia ideia da existencia disso daqui, mas fez o que eu queria
            double altura = ( a[i] * ( escala / max ) ); //usando o valor maximo do meu retângulo e do array pra criar uma escala pra altura
            
            fillRectangle(
                    xIni + ( tamanho + espaco ) * i,
                    yIni - altura,
                    tamanho,
                    altura, cor
            );
            
        }
        
    }
    
    //Preciso criar um outro método exclusivo pro MergeSort, senão a parte visual dele vai ficar toda feia comparada com as outras. 
    
    private void desenharArray(int[] a, int xIni, int yIni, int tamanho, int espaco, int max, double escala, Color cor) { //Mas ai é só eu passar o valor maximo do array primário e usar ele como base pra todo o resto

        for (int i = 0; i < a.length; i++) {

            double altura = ( a[i] * ( escala / max ) ); //usando o valor maximo do meu retângulo e do array pra criar uma escala pra altura

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
