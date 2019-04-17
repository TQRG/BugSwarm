package sic.repository;

import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import sic.modelo.Caja;
import sic.modelo.Empresa;

public interface CajaRepository extends PagingAndSortingRepository<Caja, Long>, QueryDslPredicateExecutor<Caja> {
   
      @Query("SELECT c FROM Caja c WHERE c.id_Caja = :idCaja AND c.eliminada = false")
      Caja findById(@Param("idCaja") long idCaja);
      
      Caja findByNroCajaAndEmpresaAndEliminada(int nroCaja, Empresa empresa, boolean eliminada);

      List<Caja> findAllByFechaAperturaBetweenAndEmpresaAndEliminada(Date desde, Date hasta, Empresa empresa, boolean eliminada);
      
      Caja findTopByEmpresaAndEliminadaOrderByFechaAperturaDesc(Empresa empresa, boolean eliminada);

}
