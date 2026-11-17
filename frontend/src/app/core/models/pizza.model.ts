export interface Pizza {
  id: number;
  name: string;
  descricao: string;
  categoria: string;
  valorBase: number;
}

export function arredondarPrecoPizza(valorBruto: number): number {
  const totalCentavos = Math.round(Number(valorBruto) * 100);
  const segundaCasa = ((totalCentavos % 10) + 10) % 10;

  if (segundaCasa === 5 || segundaCasa === 0) {
    return totalCentavos / 100;
  }
  if (segundaCasa < 5) {
    return (totalCentavos - segundaCasa) / 100;
  }
  return (totalCentavos + (10 - segundaCasa)) / 100;
}

export interface NovaPizza {
  name: string;
  descricao: string;
  categoria: string;
  valorBase: number;
}
