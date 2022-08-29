package com.fatec.sigx.services;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import com.fatec.sigx.model.Cliente;
import com.fatec.sigx.model.Endereco;
import com.fatec.sigx.model.ClienteRepository;

@Service
public class MantemClienteI implements MantemCliente {
	Logger logger = LogManager.getLogger(this.getClass());
	@Autowired
	ClienteRepository repository;
	public List<Cliente> consultaTodos() {
		logger.info(">>>>>> servico consultaTodos chamado");
		return repository.findAll();
	}
	@Override
	public Optional<Cliente> consultaPorCpf(String cpf) {
		logger.info(">>>>>> servico consultaPorCpf chamado");
		return repository.findByCpf(cpf);
	}
	@Override
	public Optional<Cliente> consultaPorId(Long id) {
		logger.info(">>>>>> servico consultaPorId chamado");
		return repository.findById(id);
	}
	@Override
	public Optional<Cliente> save(Cliente cliente) {
		logger.info(">>>>>> servico save chamado ");
		Optional<Cliente> umCliente = consultaPorCpf(cliente.getCpf());
		boolean dtNascimentoIsValida = validaData(cliente.getDataNascimento());
		Optional<Endereco> endereco = obtemEndereco(cliente.getCep());
	
		if (umCliente.isEmpty() & endereco.isPresent() & dtNascimentoIsValida) {
				logger.info(">>>>>> servico save - dados validos");
				cliente.setDataCadastro(new DateTime());
				cliente.setEndereco(endereco.get().getLogradouro());
				Optional<String> sexo = Optional.ofNullable(cliente.getSexo());
				if (sexo.isEmpty()) {
					logger.info(">>>>>> cliente atributo sexo => vazio");
					cliente.setSexo("M");//default
				}
                return Optional.ofNullable(repository.save(cliente));
 		} else {
		        return Optional.empty();
		}
		
	}
	@Override
	public void delete(Long id) {
		logger.info(">>>>>> servico delete por id chamado");
		repository.deleteById(id);
	}
	@Override
	public Optional<Cliente> atualiza(Cliente cliente) {
		logger.info(">>>>>> 1.servico altera cliente chamado");
		Optional<Cliente> umCliente = consultaPorId(cliente.getId());
		Optional<Endereco> endereco = obtemEndereco(cliente.getCep());
		if (umCliente.isPresent() & endereco.isPresent()) {
			Cliente clienteModificado = new Cliente(cliente.getNome(), cliente.getDataNascimento(), cliente.getSexo(),
					cliente.getCpf(), cliente.getCep(), cliente.getComplemento(), cliente.getProfissao());
			clienteModificado.setId(cliente.getId());
			clienteModificado.obtemDataAtual(new DateTime());
			clienteModificado.setEndereco(endereco.get().getLogradouro());
			clienteModificado.setProfissao(cliente.getProfissao());
			logger.info(">>>>>> 2. servico altera cliente cep valido para o id => " + clienteModificado.getId());
			return Optional.ofNullable(repository.save(clienteModificado));
		} else {
			return Optional.empty();
		}
	}
	public boolean validaData(String data) {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		df.setLenient(false); //
		try {
			df.parse(data); // data válida (exemplo 30 fev - 31 nov)
			return true;
		} catch (ParseException ex) {
			return false;
		}
	}
	public Optional<Endereco> obtemEndereco(String cep) {
		RestTemplate template = new RestTemplate();
		String url = "https://viacep.com.br/ws/{cep}/json/";
		logger.info(">>>>>> servico consultaCep - " + cep);
		ResponseEntity<Endereco> resposta = null;

		try {
			resposta = template.getForEntity(url, Endereco.class, cep);
			return Optional.ofNullable(resposta.getBody());
		} catch (ResourceAccessException e) {
			logger.info(">>>>>> consulta CEP erro nao esperado ");
			return Optional.empty();
		} catch (HttpClientErrorException e) {
			logger.info(">>>>>> consulta CEP inválido erro HttpClientErrorException =>" + e.getMessage());
			return Optional.empty();
		}
	}
}
