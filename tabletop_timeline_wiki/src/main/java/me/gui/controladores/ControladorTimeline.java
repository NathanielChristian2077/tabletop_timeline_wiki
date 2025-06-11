package me.gui.controladores;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import me.controle.*;
import me.modelo.abstracts.ElementoNarrativo;
import me.modelo.entidades.*;
import me.modelo.interfaces.Associavel;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.fx_viewer.FxViewPanel;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ControladorTimeline implements ViewerListener {
    @FXML private Label labelTituloCampanha;
    @FXML private StackPane timelineContainer;
    @FXML private TreeView<String> treeElementos;
    @FXML private ComboBox<String> visualizationMode;
    @FXML private TextField campoBusca;
    @FXML private Label statusLabel;
    @FXML private Button refreshButton;

    private Graph grafo;
    private FxViewer viewer;
    private ViewerPipe fromViewer;
    private FxViewPanel viewPanel;
    private Set<String> selectedElements = new HashSet<>();
    private boolean autoLayout = true;

    private GerenciadorEvento gerenciadorEventos = new GerenciadorEvento();
    private GerenciadorPersonagem gerenciadorPersonagens = new GerenciadorPersonagem();
    private GerenciadorLocal gerenciadorLocais = new GerenciadorLocal();
    private GerenciadorObjeto gerenciadorObjetos = new GerenciadorObjeto();

    private Campanha campanha;

    public void setCampanha(Campanha campanha) {
        this.campanha = campanha;
        labelTituloCampanha.setText(campanha.getNome());
        setupTreeView();
        updateVisualization();
    }

    @FXML
    private void initialize() {
        setupGraph();
        setupEventHandlers();
        setupVisualizationModes();
    }

    private void setupTreeView() {
        TreeItem<String> root = new TreeItem<>("Campaign " + this.campanha.getNome());
        root.setExpanded(true);

        root.getChildren().addAll(
                new TreeItem<>("Timeline"),
                new TreeItem<>("Characters"),
                new TreeItem<>("Locations"),
                new TreeItem<>("Objects"),
                new TreeItem<>("Relations")
        );

        treeElementos.setRoot(root);
        treeElementos.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) handleTreeSelection(newVal.getValue());
                });
    }

    private void setupVisualizationModes() {
        visualizationMode.getItems().addAll("Timeline", "Characters Web", "Locations Map", "General Relations", "Events by Element");
        visualizationMode.setValue("Timeline");
        visualizationMode.setOnAction(e -> updateVisualization());
    }

    private void initializeGraph() {
        System.setProperty("org.graphstream.ui", "javafx");

        grafo = new SingleGraph("Timeline");
        grafo.setAttribute("ui.stylesheet", "url('file: src/main/resources/me/gui/timeline.css')");
        grafo.setAttribute("ui.quality");
        grafo.setAttribute("ui.antialias");
        grafo.setStrict(false);
        grafo.setAutoCreate(true);
    }

    private void setupGraph() {
        initializeGraph();

        viewer = new FxViewer(grafo, FxViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        viewer.enableAutoLayout();

        viewPanel = (FxViewPanel) viewer.addDefaultView(false);
        timelineContainer.getChildren().add(viewPanel);

        fromViewer = viewer.newViewerPipe();
        fromViewer.addViewerListener((ViewerListener) this);
        fromViewer.addSink(grafo);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            public void run() {
                Platform.runLater(() -> fromViewer.pump());
            }
        }, 0, 40);
    }

    private void setupEventHandlers() {
        refreshButton.setOnAction(e -> refreshGraph());

        campoBusca.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.trim().isEmpty()) highlightSearchResults(newVal);
            else clearHighlights();
        });
    }

    private void handleTreeSelection(String selection) {
        switch (selection) {
            case "Timeline" -> visualizationMode.setValue("Timeline");
            case "Characters" -> visualizationMode.setValue("Characters Web");
            case "Locations" -> visualizationMode.setValue("Locations Map");
            case "Objects", "Relations" -> visualizationMode.setValue("General Relations");
        }
        updateVisualization();
    }

    private void updateVisualization() {
        CompletableFuture.runAsync(() -> {
            Platform.runLater(() -> statusLabel.setText("Refreshing..."));
            clearGraph();

            switch (visualizationMode.getValue()) {
                case "Timeline" -> createTimelineGraph();
                case "Characters Web" -> createCharacterNetworkGraph();
                case "Locations Map" -> createLocationMapGraph();
                case "General Relations" -> createGeneralRelationshipGraph();
                case "Events by Element" -> createEventElementGraph();
            }

            Platform.runLater(() -> {
                statusLabel.setText("Refreshed");
                if (autoLayout) viewer.enableAutoLayout();
            });
        });
    }

    private void refreshGraph() {
        updateVisualization();
    }

    private void clearGraph() {
        grafo.clear();
        selectedElements.clear();
    }

    private Node criarNo(String id, String label, String classeCSS, Object elemento) {
        Node node = grafo.getNode(id);
        if (node == null) node = grafo.addNode(id);
        node.setAttribute("ui.label", label);
        node.setAttribute("ui.class", classeCSS);
        if (elemento != null) node.setAttribute("elemento", elemento);
        return node;
    }

    private void criarAresta(String idOrigem, String idDestino, String cssClass, String prefixo) {
        if (grafo.getNode(idOrigem) == null || grafo.getNode(idDestino) == null) return;
        String id1 = prefixo + "_" + idOrigem + "_" + idDestino;
        String id2 = prefixo + "_" + idDestino + "_" + idOrigem;
        if (grafo.getEdge(id1) == null && grafo.getEdge(id2) == null) {
            Edge edge = grafo.addEdge(id1, idOrigem, idDestino);
            edge.setAttribute("ui.class", cssClass);
        }
    }

    private void createTimelineGraph() {
        List<Evento> eventos = gerenciadorEventos.listarOrdenadoPorData();
        for (int i = 0; i < eventos.size(); i++) {
            Evento evento = eventos.get(i);
            criarNo(evento.getId(), evento.getTitulo(), "evento", evento);
            if (i > 0) criarAresta(eventos.get(i-1).getId(), evento.getId(), "temporal", "seq");
        }
        for (Evento e : eventos) {
            for (Evento ant : e.getEventosRelacionados())
                criarAresta(ant.getId(), e.getId(), "relacao", "rel");
        }
    }

    private void createCharacterNetworkGraph() {
        for (Personagem p : gerenciadorPersonagens.listarTodos())
            criarNo(p.getId(), p.getNome(), "personagem", p);

        for (Evento e : gerenciadorEventos.listarTodas()) {
            List<Personagem> lista = e.getPersonagensRelacionados();
            for (int i = 0; i < lista.size(); i++)
                for (int j = i + 1; j < lista.size(); j++) {
                    String edgeId = lista.get(i).getId() + "_" + lista.get(j).getId();
                    if (grafo.getEdge(edgeId) == null) {
                        Edge edge = grafo.addEdge(edgeId, lista.get(i).getId(), lista.get(j).getId());
                        edge.setAttribute("ui.class", "personagem-relacao");
                        edge.setAttribute("eventos_compartilhados", 1);
                    } else {
                        Edge edge = grafo.getEdge(edgeId);
                        int count = (Integer) edge.getAttribute("eventos_compartilhados") + 1;
                        edge.setAttribute("eventos_compartilhados", count);
                        edge.setAttribute("ui.style", "size: " + Math.min(count * 2, 10) + "px;");
                    }
                }
        }
    }

    private void createLocationMapGraph() {
        for (Local l : gerenciadorLocais.listarTodos())
            criarNo(l.getId(), l.getNome(), "local", l);

        for (Local l : gerenciadorLocais.listarTodos())
            for (Local rel : l.getLocaisRelacionados())
                criarAresta(l.getId(), rel.getId(), "local-relacao", "loc");

        for (Evento e : gerenciadorEventos.listarTodas()) {
            if (!e.getLocaisRelacionados().isEmpty()) {
                Node eventoNode = criarNo("evt_" + e.getId(), e.getTitulo(), "evento-pequeno", e);
                for (Local l : e.getLocaisRelacionados())
                    criarAresta(eventoNode.getId(), l.getId(), "evento-local", "el");
            }
        }
    }

    private void createGeneralRelationshipGraph() {
        for (Personagem p : gerenciadorPersonagens.listarTodos())
            criarNo("p_" + p.getId(), p.getNome(), "personagem", p);
        for (Local l : gerenciadorLocais.listarTodos())
            criarNo("l_" + l.getId(), l.getNome(), "local", l);
        for (Objeto o : gerenciadorObjetos.listarTodos())
            criarNo("o_" + o.getId(), o.getNome(), "objeto", o);
        for (Evento e : gerenciadorEventos.listarTodas())
            criarNo("e_" + e.getId(), e.getTitulo(), "evento", e);

        for (Associavel a : gerenciadorPersonagens.listarTodos()) connectElementRelations("p_" + ((ElementoNarrativo) a).getId(), a);
        for (Associavel a : gerenciadorLocais.listarTodos()) connectElementRelations("l_" + ((ElementoNarrativo) a).getId(), a);
        for (Associavel a : gerenciadorObjetos.listarTodos()) connectElementRelations("o_" + ((ElementoNarrativo) a).getId(), a);
        for (Associavel a : gerenciadorEventos.listarTodas()) connectElementRelations("e_" + ((ElementoNarrativo) a).getId(), a);
    }

    private void createEventElementGraph() {
        for (Evento e : gerenciadorEventos.listarTodas()) {
            criarNo(e.getId(), e.getTitulo(), "evento-central", e);
            conectarEventoParaElemento(e, "personagem", e.getPersonagensRelacionados());
            conectarEventoParaElemento(e, "local", e.getLocaisRelacionados());
            conectarEventoParaElemento(e, "objeto", e.getObjetosRelacionados());
        }
    }

    private <T extends ElementoNarrativo> void conectarEventoParaElemento(Evento e, String tipo, List<T> lista) {
        for (T el : lista) {
            String id = tipo.charAt(0) + "_" + el.getId();
            criarNo(id, el.getNome(), tipo + "-satelite", el);
            criarAresta(e.getId(), id, "evento-" + tipo, "ev");
        }
    }

    private void connectElementRelations(String nodeId, Associavel elemento) {
        for (Personagem p : elemento.getPersonagensRelacionados()) criarAresta(nodeId, "p_" + p.getId(), "relacao-personagem", "r");
        for (Local l : elemento.getLocaisRelacionados()) criarAresta(nodeId, "l_" + l.getId(), "relacao-local", "r");
        for (Objeto o : elemento.getObjetosRelacionados()) criarAresta(nodeId, "o_" + o.getId(), "relacao-objeto", "r");
        for (Evento e : elemento.getEventosRelacionados()) criarAresta(nodeId, "e_" + e.getId(), "relacao-evento", "r");
    }

    private void highlightSearchResults(String searchText) {
        clearHighlights();
        for (Node node : grafo) {
            String label = node.getAttribute("ui.label").toString();
            if (label != null && label.toLowerCase().contains(searchText.toLowerCase())) {
                node.setAttribute("ui.class", node.getAttribute("ui.class") + " highlighted");
                selectedElements.add(node.getId());
            }
        }
    }

    private void clearHighlights() {
        for (String nodeId : selectedElements) {
            Node node = grafo.getNode(nodeId);
            if (node != null) {
                String cssClass = (String) node.getAttribute("ui.class");
                if (cssClass != null && cssClass.contains("highlighted")) {
                    cssClass = Arrays.stream(cssClass.split(" "))
                            .filter(c -> !c.equals("highlighted"))
                            .reduce((a, b) -> a + " " + b)
                            .orElse("");
                    node.setAttribute("ui.class", cssClass);
                }
            }
        }
        selectedElements.clear();
    }

    @Override
    public void viewClosed(String s) {

    }

    @Override
    public void buttonPushed(String s) {

    }

    @Override
    public void buttonReleased(String s) {

    }

    @Override
    public void mouseOver(String s) {

    }

    @Override
    public void mouseLeft(String s) {

    }
}
