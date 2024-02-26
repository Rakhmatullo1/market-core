package com.rahmatullo.comfortmarket.service.impl;

import com.rahmatullo.comfortmarket.entity.Category;
import com.rahmatullo.comfortmarket.entity.Premise;
import com.rahmatullo.comfortmarket.entity.Product;
import com.rahmatullo.comfortmarket.entity.User;
import com.rahmatullo.comfortmarket.repository.CategoryRepository;
import com.rahmatullo.comfortmarket.repository.PremiseRepository;
import com.rahmatullo.comfortmarket.repository.ProductRepository;
import com.rahmatullo.comfortmarket.service.AuthService;
import com.rahmatullo.comfortmarket.service.CategoryService;
import com.rahmatullo.comfortmarket.service.ProductService;
import com.rahmatullo.comfortmarket.service.dto.MessageDto;
import com.rahmatullo.comfortmarket.service.dto.PremiseDto;
import com.rahmatullo.comfortmarket.service.dto.ProductDto;
import com.rahmatullo.comfortmarket.service.dto.ProductRequestDto;
import com.rahmatullo.comfortmarket.service.enums.UserRole;
import com.rahmatullo.comfortmarket.service.exception.DoesNotMatchException;
import com.rahmatullo.comfortmarket.service.exception.ExistsException;
import com.rahmatullo.comfortmarket.service.exception.FileUploadException;
import com.rahmatullo.comfortmarket.service.exception.NotFoundException;
import com.rahmatullo.comfortmarket.service.mapper.CategoryMapper;
import com.rahmatullo.comfortmarket.service.mapper.PremiseMapper;
import com.rahmatullo.comfortmarket.service.mapper.ProductMapper;
import com.rahmatullo.comfortmarket.service.utils.ExcelUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final PremiseRepository premiseRepository;
    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;
    private final PremiseMapper premiseMapper;
    private final CategoryService categoryService;
    private final AuthService authService;

    @Override
    public List<ProductDto> getProductsByCategoryId(Long categoryId, PageRequest pageRequest) {
        log.info("Requested to get all products by category");
        Category category = categoryService.toCategory(categoryId);

        User owner = checkAnGetOwner();

        log.info("Successfully fetched all product which are belonged to {} {}", owner.getUsername(), owner.getRole());
        return getProducts(productRepository.getAllByCategoryAndOwner(category, owner, pageRequest).getContent());
    }

    @Override
    public List<ProductDto> getProductsByPremiseId(Long premiseId, PageRequest pageRequest) {
        log.info("Requested to get products by premise {}", premiseId);
        Premise premise = toPremise(premiseId);
        return getProducts(productRepository.getAllByPremise(premise, pageRequest).getContent());
    }

    @Override
    public List<ProductDto> getProductByOwner(PageRequest pageRequest) {
        return getProducts(productRepository.getAllByOwner(checkAnGetOwner(), pageRequest).getContent());
    }

    @Override
    public ProductDto getById(Long id) {
        User owner = checkAnGetOwner();

        Product product = productRepository.findByIdAndOwner(id, owner).orElseThrow(()->new NotFoundException("Product is not found"));

        return productMapper.toProductDto(product);
    }

    @Override
    public ProductDto updateProduct(Long id, ProductRequestDto productRequestDto) {
        log.warn("Requested to update product with id {}", id);
        Product product = toProduct(id, checkAnGetOwner());

        if(!Objects.equals(product.getCategory().getId(), productRequestDto.getCategoryId())){
            removeProductFromCategory(product);
            Category category = categoryService.toCategory(productRequestDto.getCategoryId());
            product.setCategory(category);
            addProduct2Category( product);
        }

        product = productMapper.toProduct(productRequestDto, product);

        log.info("Successfully updated");
        return productMapper.toProductDto(productRepository.save(product));
    }

    @Override
    public ProductDto transfersProduct(Long id, Long premiseId) {
        log.info("Requested to transfer products to premise {}", premiseId);
        Product product = toProduct(id, checkAnGetOwner());

        Premise premise = toPremise(premiseId);

        removeProductsFromPremise(product);
        product.setPremise(premise);

        List<Product> productList = premise.getProducts();
        productList.add(product);
        premise.setProducts(productList);

        premiseRepository.save(premise);
        return productMapper.toProductDto(productRepository.save(product));
    }

    @Override
    public MessageDto deleteProduct(Long id) {
        log.info("Requested to delete product {}", id);
        User owner = checkAnGetOwner();
        Product product = toProduct(id, owner);

        removeProductFromCategory( product);
        removeProductsFromPremise(product);
        productRepository.delete(product);
        log.info("Successfully deleted");
        return new MessageDto("Successfully deleted");
    }

    @Override
    public MessageDto convertXLSFile2Products(MultipartFile file) {
        log.info("Requested to convert xsl file to database");
        if(Objects.isNull(file)|| file.isEmpty()) {
            log.warn("file is null or empty ");
            throw new FileUploadException("File empty or null");
        }

        if(!ExcelUtils.hasExcelFormat(file)) {
            log.warn("Content type does not match with excel file");
            throw new DoesNotMatchException("Content type does not match with excel file");
        }

        try {
            Map<Integer, List<ProductRequestDto>> products = ExcelUtils.excelToProducts(file.getInputStream());

            products.keySet().forEach(productKey->{
                products.get(productKey).forEach(product->{
                    addProductsToPremise(Long.valueOf(productKey), product);
                });
            });

            return new MessageDto("Successfully fetched all data");
        } catch (IOException e) {
            throw new FileUploadException(e.getMessage());
        }
    }

    private List<ProductDto> getProducts(List<Product> products) {
        return products.stream().map(productMapper::toProductDto).toList();
    }

    @Override
    public Product toProduct(Long id){
        return productRepository.findById(id).orElseThrow(()->new NotFoundException("Product is not found"));
    }

    @Override
    public void addProduct2Category( Product product) {
        Category category = categoryService.toCategory(product.getCategory().getId());

        List<Product> products =category.getProductList();
        products.add(product);

        categoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public void removeProductsFromPremise(Product product) {
        log.info("Requested to remove product  {}  from {}", product.getName(), product.getPremise().getId());
        Premise premise = toPremise(product.getPremise().getId());

        List<Product> products = premise.getProducts();
        products.remove(product);
        premise.setProducts(products);

        premiseRepository.save(premise);
        log.info("successfully deleted");
    }

    @Override
    public void removeProductFromCategory( Product product) {
        log.info("Requested to remove {} from category {}", product.getName(), product.getCategory().getId());
        Category category = categoryService.toCategory(product.getCategory().getId());

        List<Product> products = category.getProductList();
        products.remove(product);
        category.setProductList(products);

        categoryRepository.save(category);
        log.info("Successfully deleted");
    }

    @Override
    public PremiseDto addProductsToPremise(Long id, ProductRequestDto productRequestDto) {
        log.info("Requested to add new products to premise {}", id);
        Premise premise = toPremise(id);

        Product product = productMapper.toProduct(productRequestDto,premise, authService.getUser());

        if(productRepository.existsByBarcode(productRequestDto.getBarcode())){
            log.warn("the product exists");
            throw new ExistsException("The product exists "+ productRequestDto.getBarcode());
        }

        User owner = checkAndGetOwner(premise);

        product.setOwner(owner);
        product = productRepository.save(product);

        addProduct2Category( product);

        List<Product> productList = premise.getProducts();
        productList.add(product);
        premise.setProducts(productList);

        return premiseMapper.toPremiseDto(premiseRepository.save(premise));
    }

    private User checkAndGetOwner(Premise premise) {
        User owner = authService.getUser();

        if(!Objects.equals(owner.getRole(), UserRole.OWNER)) {
            owner = owner.getOwner();
        }

        if(!Objects.equals(premise.getOwner(), owner)) {
            log.warn("This premise does not match with owner's premise");
            throw new DoesNotMatchException("Premise does not match with owner's premise");
        }
        return owner;
    }

    public Premise toPremise(Long id) {
        return premiseRepository.findById(id).orElseThrow(()->{
            log.warn("premise is not found");
            throw new NotFoundException("Premise is not found");
        });
    }

    private User checkAnGetOwner() {
        return authService.getOwner();
    }

    private Product toProduct(Long id, User owner) {
        return productRepository.findByIdAndOwner(id, owner).orElseThrow(()->new NotFoundException("the product has not found"));
    }
}
