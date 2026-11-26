INSERT INTO produto (id, categoria, descricao, nome, preco, precog, precom, imagem) VALUES
(3, 'Bebida', '2l', 'Coca-Cola', 15.00, NULL, NULL, NULL),
(4, 'Sobremesa', 'Mousse De Maracujá Caseiro', 'Mousse de Maracujá', 14.00, NULL, NULL, 'mousse-de-maracuja.jpg'),
(6, 'Acompanhamento', 'Batata Canoa frita e temperada', 'BATATA FRITA', 8.00, 14.00, 12.00, 'batata-frita.jpg'),
(8, 'Hambúrguer', 'Pão, Hamburguer Bovino, Queijo Mussarela e Maionese', 'X-Burguer', 20.00, NULL, NULL, 'x-burguer.jpg'),
(9, 'Hambúrguer', 'Pão, Hamburguer Bovino, Queijo Mussarela, Alface, Tomate e Maionese', 'X-Salada', 23.50, NULL, NULL, 'x-salada.jpg'),
(10, 'Hambúrguer', 'Pão, Hamburguer Bovino, Queijo Cheddar, Bacon e Maionese', 'Cheddar Bacon', 24.00, NULL, NULL, 'cheddar-bacon.jpg'),
(11, 'Hambúrguer', 'Pão, 2 Hamburgueres Bovinos, 2 Fatias de Queijo Mussarela, Rúcula, Cebola Roxa e Maionese', 'Cabuloso', 32.00, NULL, NULL, 'cabuloso.jpg'),
(12, 'Sobremesa', 'Brownie de Chocolate acompanhado com 2 bolas de sorvete', 'Brownie', 16.00, NULL, NULL, 'brownie.jpg'),
(13, 'Sobremesa', 'Petit Gatteu Recheado de chocolate acompanhado de 2 bolas de sorvete', 'Petit Gatteu', 20.00, NULL, NULL, 'petit-gatteu.jpg')
ON DUPLICATE KEY UPDATE imagem = COALESCE(imagem, VALUES(imagem));