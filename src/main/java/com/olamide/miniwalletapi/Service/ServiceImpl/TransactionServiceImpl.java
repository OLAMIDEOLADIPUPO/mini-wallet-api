    package com.olamide.miniwalletapi.Service.ServiceImpl;
    
    import com.olamide.miniwalletapi.DTO.DepositRequestDTO;
    import com.olamide.miniwalletapi.DTO.TransactionResponseDTO;
    import com.olamide.miniwalletapi.DTO.TransferRequestDTO;
    import com.olamide.miniwalletapi.DTO.WithdrawRequestDTO;
    import com.olamide.miniwalletapi.Exceptions.InvalidTransactionException;
    import com.olamide.miniwalletapi.Exceptions.WalletDeactivatedException;
    import com.olamide.miniwalletapi.Exceptions.WalletNotFoundException;
    import com.olamide.miniwalletapi.Models.Transaction;
    import com.olamide.miniwalletapi.Models.Wallet;
    import com.olamide.miniwalletapi.Repository.TransactionRepository;
    import com.olamide.miniwalletapi.Repository.WalletRepository;
    import com.olamide.miniwalletapi.Service.TransactionService;
    import com.olamide.miniwalletapi.TransactionType;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import java.util.List;

    @Service
    public class TransactionServiceImpl implements TransactionService {
        private final TransactionRepository transactionRepository;
        private final WalletRepository walletRepository;
    
        public TransactionServiceImpl(TransactionRepository transactionRepository, WalletRepository walletRepository) {
            this.transactionRepository = transactionRepository;
            this.walletRepository = walletRepository;
        }
    
        public TransactionResponseDTO save(Transaction transaction) {
            Transaction saved = transactionRepository.save(transaction);
            return new TransactionResponseDTO(saved.getSourceWalletId(),
                    saved.getDestinationWalletId(),
                                                saved.getAmount(),
                                                saved.getType(),
                                                saved.getTimestamp());
        }

        @Override
        @Transactional
        public TransactionResponseDTO deposit(DepositRequestDTO request) {
                Wallet found = walletRepository.findById(request.destinationWalletId()).orElseThrow(() -> new WalletNotFoundException("Wallet with id " +request.destinationWalletId()+ " not found") );
            if (!found.isActive()) {
                throw new WalletDeactivatedException("Cannot deposit: Destination wallet is deactivated.");
            }

                found.credit(request.amount());
    
                Transaction newTransaction = new Transaction(TransactionType.DEPOSIT,request.amount(),null,request.destinationWalletId());
                walletRepository.save(found);
                return save(newTransaction);
    
    
    
        }

        @Override
        @Transactional
        public TransactionResponseDTO withdraw(WithdrawRequestDTO request) {
            Wallet found = walletRepository.findById(request.sourceWalletId()).orElseThrow(() -> new WalletNotFoundException("Wallet with id " +request.sourceWalletId()+ " not found") );
            if (!found.isActive()) {
                throw new WalletDeactivatedException("Cannot withdraw: Source wallet is deactivated.");
            }
            found.debit(request.amount());
            Transaction newTransaction = new Transaction(TransactionType.WITHDRAW,request.amount(), request.sourceWalletId(),null);
            walletRepository.save(found);
    
            return save(newTransaction);
    
    
        }

        @Override
        @Transactional
        public TransactionResponseDTO transfer(TransferRequestDTO transferRequest) {
            if (transferRequest.sourceWalletId().equals(transferRequest.destinationWalletId())) {
                throw new InvalidTransactionException("Transfer failed: Source and destination wallets cannot be the same account.");
            }
            Wallet sender = walletRepository.findById(transferRequest.sourceWalletId()).orElseThrow(() -> new WalletNotFoundException("Wallet with id " +transferRequest.sourceWalletId()+ " not found") );
            Wallet receiver = walletRepository.findById(transferRequest.destinationWalletId()).orElseThrow(() -> new WalletNotFoundException("Wallet with id " +transferRequest.destinationWalletId()+ " not found"));

            if (!sender.isActive()) {
                throw new WalletDeactivatedException("Transfer failed: Sender account is deactivated.");
            }
            if (!receiver.isActive()) {
                throw new WalletDeactivatedException("Transfer failed: Destination account is deactivated.");
            }
            sender.debit(transferRequest.amount());
            receiver.credit(transferRequest.amount());
    
            Transaction transfer = new Transaction(TransactionType.TRANSFER,
                    transferRequest.amount(),
                    transferRequest.sourceWalletId(),
                    transferRequest.destinationWalletId());
            walletRepository.save(sender);
            walletRepository.save(receiver);
            return save(transfer);
    
    
        }

        @Override
        public TransactionResponseDTO findById(Long Id) {
            return (transactionRepository.findById(Id).map(
                    transaction -> new TransactionResponseDTO(
                            transaction.getSourceWalletId(),
                            transaction.getDestinationWalletId(),
                            transaction.getAmount(),
                            transaction.getType(),
                            transaction.getTimestamp()
                    )
            ).orElseThrow(() -> new WalletNotFoundException("No transaction with id " + Id)));
        }


        @Override
        public List<TransactionResponseDTO> getTransactionHistory(Long walletId) {
            return transactionRepository.findAllByWalletId(walletId)
                    .stream()
                    .map(transaction -> new TransactionResponseDTO(
                            transaction.getDestinationWalletId(),
                            transaction.getSourceWalletId(),
                            transaction.getAmount(),
                            transaction.getType(),
                            transaction.getTimestamp()
                    ))
                    .toList();
        }
    }
