package sic.repository;

import java.util.List;
import sic.modelo.BusquedaPedidoCriteria;
import sic.modelo.Pedido;
import sic.modelo.RenglonPedido;

public interface IPedidoRepository {

    void actualizar(Pedido pedido);

    long buscarMayorNroPedido(long idEmpresa);

    List<Pedido> buscarPedidosPorCriteria(BusquedaPedidoCriteria criteria);

    Pedido getPedidoPorNro(long nroPedido, long idEmpresa);

    Pedido getPedidoPorNumeroConFacturas(long nroPedido);

    Pedido getPedidoPorNumeroConRenglones(long nroPedido, long idEmpresa);

    List<RenglonPedido> getRenglonesDelPedido(long nroPedido);

    void guardar(Pedido pedido);

}
