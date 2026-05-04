package org.rj.mylelo.elomyl;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.rj.mylelo.elomyl.dao.GenericDao;
import org.rj.mylelo.elomyl.model.TournamentView;

@Slf4j
public class CalcularElo {
    
    private List<TournamentView> allMatches;
    
    public CalcularElo(){
        this.allMatches = new ArrayList<>();
    }
    
    public void calcularElo(){
        allMatches = GenericDao.getAllTournamentsView();
        log.info("{} match por procesar", allMatches.size());
        
        
    }
    
}
