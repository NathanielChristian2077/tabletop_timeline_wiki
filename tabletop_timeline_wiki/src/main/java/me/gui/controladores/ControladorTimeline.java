package me.gui.controladores;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import me.controle.GerenciadorEvento;
import me.controle.GerenciadorLocal;
import me.controle.GerenciadorObjeto;
import me.controle.GerenciadorPersonagem;
import me.modelo.entidades.*;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.fx_viewer.FxDefaultView;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.view.Viewer;

public class ControladorTimeline {
    @FXML private Label labelTituloCampanha;
    @FXML private StackPane timelineContainer;
    @FXML private TreeView<String> treeElementos;
    @FXML private TextField campoBusca;

    private Graph grafo;
    private FxViewer viewer;
    private FxDefaultView view;

    private GerenciadorEvento gerenciadorEvento = new GerenciadorEvento();
    private GerenciadorPersonagem gerenciadorPersonagem = new GerenciadorPersonagem();
    private GerenciadorLocal gerenciadorLocal = new GerenciadorLocal();
    private GerenciadorObjeto gerenciadorObjeto = new GerenciadorObjeto();

    private Campanha campanha;
    
        public void setCampanha(Campanha campanha) {
            this.campanha = campanha;
            labelTituloCampanha.setText(campanha.getNome());
    }

    @FXML
    private void initialize() {
            setupGraph();
    }

    private void setupGraph() {
            grafo = new SingleGraph("Timeline");
            grafo.setAttribute("ui.stylesheet", "url('file: src/main/resources/me/gui/timeline.css')");
            grafo.setStrict(false);
            grafo.setAutoCreate(true);

            for (Evento evento : gerenciadorEvento.listarTodas()) {
                adicionarEvento(evento);
            }

            viewer = new FxViewer(grafo, FxViewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
            viewer.enableAutoLayout();
            view = (FxDefaultView) viewer.addDefaultView(false);
            timelineContainer.getChildren().add(view);
    }

    public void adicionarEvento(Evento evento) {
            String nodeId = evento.getId();
            grafo.addNode(nodeId).setAttribute("ui.label", evento.getNome());

            for (Evento posterior : evento.getEventosRelacionados()) {
                if (!grafo.getNode(posterior.getId()).hasEdgeBetween(nodeId)) {
                    grafo.addEdge(evento.getId() + "->" + posterior.getId(), nodeId, posterior.getId(), true);
                }
            }

            atualizarEvento(evento);
    }

    private void atualizarEvento(Evento evento) {
            if (grafo.getNode(evento.getId()) == null) return;

            int totalRelacionados = evento.getEventosRelacionados().size()
                                    + evento.getLocaisRelacionados().size()
                                    + evento.getPersonagensRelacionados().size()
                                    + evento.getObjetosRelacionados().size();

            double baseSize = 25.0;
            double growthFactor = 3.0;
            double finalSize = baseSize + (totalRelacionados * growthFactor);

            grafo.getNode(evento.getId()).setAttribute("ui.style", "-fx-size:"+ finalSize +"-fx-text-size: 18px");
    }

    private void expandirEvento(Evento evento) {
            Node nodeEvento = grafo.getNode(evento.getId());
            if (nodeEvento == null) return;

            //Personagens
            for(Personagem p : evento.getPersonagensRelacionados()) {
                if (grafo.getNode(p.getId()) == null) {
                    grafo.addNode(p.getId()).setAttribute("ui.label", p.getId());
                    grafo.getNode(p.getId()).setAttribute("ui.class", "personagem");
                }
                if (grafo.getEdge(evento.getId() + "-" + p.getId()) == null) {
                    grafo.addEdge(evento.getId() + "-" + p.getId(), evento.getId(), p.getId());
                }
            }

            //Locais
            for(Local l : evento.getLocaisRelacionados()) {
                if (grafo.getNode(l.getId()) == null) {
                    grafo.addNode(l.getId()).setAttribute("ui.label", l.getId());
                    grafo.getNode(l.getId()).setAttribute("ui.class", "local");
                }
                if (grafo.getEdge(evento.getId() + "-" + l.getId()) == null) {
                    grafo.addEdge(evento.getId() + "-" + l.getId(), evento.getId(), l.getId());
                }
            }
            //Objetos
            for(Objeto o : evento.getObjetosRelacionados()) {
                if (grafo.getNode(o.getId()) == null) {
                    grafo.addNode(o.getId()).setAttribute("ui.label", o.getId());
                    grafo.getNode(o.getId()).setAttribute("ui.class", "objeto");
                }
                if (grafo.getEdge(evento.getId() + "-" + o.getId()) == null) {
                    grafo.addEdge(evento.getId() + "-" + o.getId(), evento.getId(), o.getId());
                }
            }

            atualizarEvento(evento);
    }

    //@Override
    public void mouseClicked(MouseEvent e) {
        //GraphicElement element = view.findGraphicElementAt(e.getX(), e.getY());????
        //if (element != null) {
        //    //TODO
        //}
    }
}
