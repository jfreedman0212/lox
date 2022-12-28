use bytes::Buf;

use crate::{
    errors::VirtualMachineError,
    values::{Value, OP_ADD, OP_CONSTANT, OP_DIVIDE, OP_MULTIPLY, OP_NEGATE, OP_SUBTRACT},
};

pub struct VirtualMachine<BufferType: Buf> {
    operations: BufferType,
    value_stack: Vec<Value>,
    constants: Vec<Value>,
}

impl<BufferType: Buf> VirtualMachine<BufferType> {
    pub fn new(operations: BufferType, constants: Vec<Value>) -> Self {
        Self {
            operations,
            value_stack: Vec::with_capacity(256),
            constants,
        }
    }

    pub fn run(&mut self) -> Result<(), VirtualMachineError> {
        while self.operations.has_remaining() {
            let byte = self.operations.get_u8();
            match byte {
                OP_CONSTANT => {
                    if !self.operations.has_remaining() {
                        panic!("OP_CONSTANT opcode should be two bytes long!");
                    }
                    let constant_index = self.operations.get_u8();
                    if let Some(constant) = self.constants.get(constant_index as usize) {
                        self.push_to_stack(constant.clone());
                    }
                }
                binary_operation
                    if binary_operation == OP_ADD
                        || binary_operation == OP_SUBTRACT
                        || binary_operation == OP_DIVIDE
                        || binary_operation == OP_MULTIPLY =>
                {
                    let right = self.pop_from_stack();
                    let left = self.pop_from_stack();
                    match (left, right) {
                        (Some(Value::Number(left)), Some(Value::Number(right))) => {
                            let (operation, result) = match binary_operation {
                                OP_ADD => ('+', left + right),
                                OP_SUBTRACT => ('-', left - right),
                                OP_DIVIDE => ('/', left / right),
                                OP_MULTIPLY => ('*', left * right),
                                _ => panic!("Unreachable code"),
                            };
                            println!("{} {} {} = {}", left, operation, right, result);
                            self.push_to_stack(Value::Number(result));
                        }
                        err => {
                            panic!("Unexpected combination of addends: {:?}", err);
                        }
                    }
                }
                OP_NEGATE => match self.pop_from_stack() {
                    Some(Value::Number(operand)) => self.push_to_stack(Value::Number(-operand)),
                    Some(value) => panic!("Expected a Number value, got {:?} instead", value),
                    None => panic!("No value on the stack to pop"),
                },
                unknown => {
                    panic!("Received unknown opcode: {}", unknown);
                }
            }
        }
        Ok(())
    }

    fn push_to_stack(&mut self, value: Value) {
        self.value_stack.push(value);
    }

    fn pop_from_stack(&mut self) -> Option<Value> {
        self.value_stack.pop()
    }
}
