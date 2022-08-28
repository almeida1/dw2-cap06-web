package com.fatec.sigx.services;
import java.util.List;
import java.util.Optional;

import com.fatec.sigx.model.Cliente;
import com.fatec.sigx.model.Endereco;
public interface MantemCliente {
	List<Cliente> consultaTodos();
	Optional<Cliente> consultaPorCpf(String cpf);
	Optional<Cliente> consultaPorId(Long id);
	Optional<Cliente> save(Cliente cliente);
	void delete (Long id);
	Optional<Cliente> atualiza ( Cliente cliente);
	public boolean validaData(String data);
	Optional<Endereco> obtemEndereco(String cep);
	
}
