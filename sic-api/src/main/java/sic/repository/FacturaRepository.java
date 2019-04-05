package sic.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import sic.modelo.Empresa;
import sic.modelo.Factura;
import sic.modelo.Pedido;

public interface FacturaRepository extends PagingAndSortingRepository<Factura, Long>, FacturaRepositoryCustom {

    @Query("SELECT fv FROM FacturaVenta fv WHERE fv.id_Factura = :idFactura AND fv.eliminada = false")
    Factura findById(@Param("idFactura") long idFactura);
    
    Factura findByTipoFacturaAndNumSerieAndNumFacturaAndEmpresaAndEliminada(char tipo, long serie, long num, Empresa empresa, boolean eliminada);

    List<Factura> findAllByPedidoAndEliminada(Pedido pedido, boolean eliminada);

    @Query("SELECT max(fv.numFactura) FROM FacturaVenta fv WHERE fv.tipoFactura = :tipoDeFactura AND fv.numSerie = :numSerie AND fv.empresa.id_Empresa = :idEmpresa")
    Long buscarMayorNumFacturaSegunTipo(@Param("tipoDeFactura") char tipoDeFactura, @Param("numSerie") long numSerie, @Param("idEmpresa") long idEmpresa);

}
