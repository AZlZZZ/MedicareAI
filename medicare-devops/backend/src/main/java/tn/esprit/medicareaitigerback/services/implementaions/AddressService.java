package tn.esprit.medicareaitigerback.services.implementaions;

import  tn.esprit.medicareaitigerback.exceptions.AddressDontMatchWithUserException;
import  tn.esprit.medicareaitigerback.exceptions.AddressNotFoundException;
import tn.esprit.medicareaitigerback.exceptions.PatientNotFoundException;
import  tn.esprit.medicareaitigerback.models.dto.mapper.AddressMapper;
import  tn.esprit.medicareaitigerback.models.dto.request.AddressAddDTO;
import  tn.esprit.medicareaitigerback.models.dto.request.AddressUpdDTO;
import  tn.esprit.medicareaitigerback.models.dto.response.AddressDTO;
import  tn.esprit.medicareaitigerback.models.entities.Address;
import  tn.esprit.medicareaitigerback.models.entities.User;
import  tn.esprit.medicareaitigerback.repositories.AddressRepository;
import tn.esprit.medicareaitigerback.repositories.PatientRepository;
import  tn.esprit.medicareaitigerback.services.interfaces.IAddressService;
import tn.esprit.medicareaitigerback.services.interfaces.IPatientService;
import  tn.esprit.medicareaitigerback.specification.SearchCriteria;
import  tn.esprit.medicareaitigerback.specification.SearchCriteriaType;
import  tn.esprit.medicareaitigerback.specification.SearchOperation;
import  tn.esprit.medicareaitigerback.specification.SpecificationsBuilder;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AddressService implements IAddressService {

    private AddressRepository addressRepository;

    private IPatientService clientService;
    private PatientRepository clientRepository;
    private AddressMapper addressMapper;

    @Override
    @Transactional
    public AddressDTO add(AddressAddDTO dto) {
        var address = new Address();
        address = addressMapper.toEntity(dto);
//        address.setUser(user);

        address = addressRepository.save(address);
        return addressMapper.toDTO(address);
    }



    @Override
    @Transactional
    public void update(Long addressId, AddressUpdDTO dto) throws AddressNotFoundException, AddressDontMatchWithUserException {
//        var userDetailsImpl = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var address = addressRepository.findById(addressId).orElseThrow(() -> new AddressNotFoundException("Address not found " + addressId));
        User user = null;

//        if (userDetailsImpl.getUserType() == UserType.CLIENT) {
//            user = clientService.findByUserName(userDetailsImpl.getUsername());
//        }

        if (user != null && !user.getId().equals(address.getUser().getId())) {
            throw new AddressDontMatchWithUserException("address dont match with user " + addressId);
        }

        if (dto.getAddress() != null && !dto.getAddress().isBlank()) {
            address.setAddress(dto.getAddress());
        }
        if (dto.getLibelle() != null && !dto.getLibelle().isBlank()) {
            address.setLibelle(dto.getLibelle());
        }

        if (dto.getLatitude() != null) {
            address.setLatitude(dto.getLatitude());
        }
        if (dto.getLongitude() != null) {
            address.setLongitude(dto.getLongitude());
        }
        addressRepository.save(address);
    }

    @Override
    public void delete(Long addressId) throws  AddressNotFoundException, AddressDontMatchWithUserException {
//        var userDetailsImpl = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var address = addressRepository.findById(addressId).orElseThrow(() -> new AddressNotFoundException("Address not found " + addressId));
        User user = null;


//        if (userDetailsImpl.getUserType() == UserType.CLIENT) {
//            user = clientService.findByUserName(userDetailsImpl.getUsername());
//        }
        if (user != null && !user.getId().equals(address.getUser().getId())) {
            throw new AddressDontMatchWithUserException("address dont match with user " + addressId);
        }

        addressRepository.deleteById(addressId);
    }

    @Override
    public List<AddressDTO> findAllByClientId(Long clientId) throws PatientNotFoundException {
        clientRepository.findById(clientId).orElseThrow(() -> new PatientNotFoundException("Client not found " + clientId));
        var specificationList = new ArrayList<SearchCriteria>();
        specificationList.add(new SearchCriteria("user_id", SearchOperation.GROUP_JOIN_EQUALITY, clientId.toString(), SearchCriteriaType.DEFAULT, "user", "id"));
        return addressRepository.findAll(new SpecificationsBuilder<Address>(specificationList).build()).stream().map(addressMapper::toDTO).collect(Collectors.toList());
    }
}
