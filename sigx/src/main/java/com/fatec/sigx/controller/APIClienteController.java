package com.fatec.sigx.controller;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fatec.sigx.model.Cliente;
import com.fatec.sigx.model.ClienteDTO;
import com.fatec.sigx.model.Endereco;
import com.fatec.sigx.services.MantemCliente;

@RestController
@RequestMapping("/api/v1/clientes")
/*
 * Trata as requisicoes HTTP enviadas pelo usuario do servico
 */
public class APIClienteController {
	@Autowired
	MantemCliente servico;
	Cliente cliente;
	Logger logger = LogManager.getLogger(this.getClass());

	@PostMapping
	public ResponseEntity<Object> saveCliente(@RequestBody @Valid ClienteDTO clienteDTO) {
		cliente = new Cliente();
		if (servico.consultaPorCpf(clienteDTO.getCpf()).isPresent()) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("CPF já cadastrado");
		}

		try {
			cliente.setDataNascimento(clienteDTO.getDataNascimento());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
		Optional<Endereco> endereco = Optional.ofNullable(servico.obtemEndereco(clienteDTO.getCep()));
		logger.info(">>>>>> controller post " + clienteDTO.getCep());
		if (endereco.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("CEP invalido");
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(servico.save(clienteDTO.retornaUmCliente()));
	}

	@GetMapping
	public ResponseEntity<List<Cliente>> consultaTodos() {
		return ResponseEntity.status(HttpStatus.OK).body(servico.consultaTodos());
	}
    @GetMapping("/{id}")
    public ResponseEntity<Object> consultaPorId(@PathVariable (value="id") Long id){
    	Optional<Cliente> cliente = servico.consultaPorId(id);
    	if (cliente.isEmpty()) {
    		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id não encontrado.");
    	}
    	return ResponseEntity.status(HttpStatus.OK).body(cliente.get());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletePorId(@PathVariable (value="id") Long id){
    	Optional<Cliente> cliente = servico.consultaPorId(id);
    	if (cliente.isEmpty()) {
    		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id não encontrado.");
    	}
    	servico.delete(cliente.get().getId());
    	return ResponseEntity.status(HttpStatus.OK).body("Cliente excluido");
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> atualiza(@PathVariable (value="id") Long id, @RequestBody @Valid ClienteDTO clienteDTO){
    	Optional<Cliente> c = servico.consultaPorId(id);
    	if (c.isEmpty()) {
    		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id não encontrado.");
    	}
    	Optional<Endereco> e = Optional.ofNullable(servico.obtemEndereco(clienteDTO.getCep()));
    	if (e.isEmpty()) {
    		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("CEP não localizado.");
    	}
    	servico.atualiza(clienteDTO.retornaUmCliente());
    	return ResponseEntity.status(HttpStatus.OK).body("Informações de cliente atualizada");
    }
}


